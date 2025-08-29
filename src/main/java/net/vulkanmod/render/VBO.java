package net.vulkanmod.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.VRenderSystem;
import net.vulkanmod.vulkan.memory.MemoryType;
import net.vulkanmod.vulkan.memory.MemoryTypes;
import net.vulkanmod.vulkan.memory.buffer.IndexBuffer;
import net.vulkanmod.vulkan.memory.buffer.VertexBuffer;
import net.vulkanmod.vulkan.memory.buffer.index.AutoIndexBuffer;
import net.vulkanmod.vulkan.shader.GraphicsPipeline;
import net.vulkanmod.vulkan.texture.VTextureSelector;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class VBO {

    private final MemoryType memoryType;
    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;

    private VertexFormat.Mode mode;
    private boolean autoIndexed = false;
    private int indexCount;
    private int vertexCount;

    public VBO(com.mojang.blaze3d.vertex.VertexBuffer.Usage usage) {
        this.memoryType = usage == com.mojang.blaze3d.vertex.VertexBuffer.Usage.STATIC
                ? MemoryTypes.GPU_MEM
                : MemoryTypes.HOST_MEM;
    }

    public void upload(MeshData meshData) {
        MeshData.DrawState parameters = meshData.drawState();

        this.indexCount = parameters.indexCount();
        this.vertexCount = parameters.vertexCount();
        this.mode = parameters.mode();

        this.uploadVertexBuffer(parameters, meshData.vertexBuffer());
        this.uploadIndexBuffer(meshData.indexBuffer());

        meshData.close();
    }

    private void uploadVertexBuffer(MeshData.DrawState parameters, ByteBuffer data) {
        if (data != null) {
            if (this.vertexBuffer != null) {
                this.vertexBuffer.scheduleFree();
            }

            int size = parameters.format().getVertexSize() * parameters.vertexCount();
            this.vertexBuffer = new VertexBuffer(size, this.memoryType);
            this.vertexBuffer.copyBuffer(data, size);
        }
    }

    public void uploadIndexBuffer(ByteBuffer data) {
        if (data == null) {
            AutoIndexBuffer autoIndexBuffer;
            switch (this.mode) {
                case TRIANGLE_FAN -> {
                    autoIndexBuffer = Renderer.getDrawer().getTriangleFanIndexBuffer();
                    this.indexCount = AutoIndexBuffer.DrawType.getTriangleStripIndexCount(this.vertexCount);
                }
                case TRIANGLE_STRIP, LINE_STRIP -> {
                    autoIndexBuffer = Renderer.getDrawer().getTriangleStripIndexBuffer();
                    this.indexCount = AutoIndexBuffer.DrawType.getTriangleStripIndexCount(this.vertexCount);
                }
                case QUADS -> {
                    autoIndexBuffer = Renderer.getDrawer().getQuadsIndexBuffer();
                }
                case LINES -> {
                    autoIndexBuffer = Renderer.getDrawer().getLinesIndexBuffer();
                }
                case DEBUG_LINE_STRIP -> {
                    autoIndexBuffer = Renderer.getDrawer().getDebugLineStripIndexBuffer();
                }
                case TRIANGLES, DEBUG_LINES -> {
                    autoIndexBuffer = null;
                }
                default -> throw new IllegalStateException("Unexpected draw mode: %s".formatted(this.mode));
            }

            if (this.indexBuffer != null && !this.autoIndexed) {
                this.indexBuffer.scheduleFree();
            }

            if (autoIndexBuffer != null) {
                autoIndexBuffer.checkCapacity(this.vertexCount);
                this.indexBuffer = autoIndexBuffer.getIndexBuffer();
            }

            this.autoIndexed = true;
        } else {
            if (this.indexBuffer != null && !this.autoIndexed) {
                this.indexBuffer.scheduleFree();
            }

            this.indexBuffer = new IndexBuffer(data.remaining(), MemoryTypes.GPU_MEM);
            this.indexBuffer.copyBuffer(data, data.remaining());

            // Р’РђР–РќРћ: СЂР°Р· Р±СѓС„РµСЂ СЃРІРѕР№, Р° РЅРµ Р°РІС‚Рѕ вЂ” С„Р»Р°Рі РґРѕР»Р¶РµРЅ Р±С‹С‚СЊ false,
            // С‡С‚РѕР±С‹ close() РєРѕСЂСЂРµРєС‚РЅРѕ РµРіРѕ РѕСЃРІРѕР±РѕР¶РґР°Р».
            this.autoIndexed = false;
        }
    }

    public void drawWithShader(Matrix4f modelView, Matrix4f projection, ShaderInstance shaderInstance) {
        if (this.indexCount != 0) {
            RenderSystem.assertOnRenderThread();

            RenderSystem.setShader(() -> shaderInstance);

            VRenderSystem.applyMVP(modelView, projection);
            VRenderSystem.setPrimitiveTopologyGL(this.mode.asGLMode);

            // РџРµСЂРµРґР°С‘Рј Р°РєС‚СѓР°Р»СЊРЅС‹Р№ СЂРµР¶РёРј, Р° РЅРµ РІСЃРµРіРґР° QUADS
            shaderInstance.setDefaultUniforms(this.mode, modelView, projection, Minecraft.getInstance().getWindow());
            shaderInstance.apply();

            if (this.indexBuffer != null) {
                Renderer.getDrawer().drawIndexed(this.vertexBuffer, this.indexBuffer, this.indexCount);
            } else {
                Renderer.getDrawer().draw(this.vertexBuffer, this.vertexCount);
            }

            // Reset MVP to previous state
            VRenderSystem.applyMVP(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix());
        }
    }

    public void drawWithShader(Matrix4f modelView, Matrix4f projection, GraphicsPipeline pipeline) {
        if (this.indexCount != 0) {
            RenderSystem.assertOnRenderThread();

            VRenderSystem.applyMVP(modelView, projection);
            VRenderSystem.setPrimitiveTopologyGL(this.mode.asGLMode);

            Renderer renderer = Renderer.getInstance();
            renderer.bindGraphicsPipeline(pipeline);
            VTextureSelector.bindShaderTextures(pipeline);
            renderer.uploadAndBindUBOs(pipeline);

            if (this.indexBuffer != null) {
                Renderer.getDrawer().drawIndexed(this.vertexBuffer, this.indexBuffer, this.indexCount);
            } else {
                Renderer.getDrawer().draw(this.vertexBuffer, this.vertexCount);
            }

            // Reset MVP to previous state
            VRenderSystem.applyMVP(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix());
        }
    }

    public void draw() {
        if (this.indexCount != 0) {
            if (this.indexBuffer != null) {
                Renderer.getDrawer().drawIndexed(this.vertexBuffer, this.indexBuffer, this.indexCount);
            } else {
                Renderer.getDrawer().draw(this.vertexBuffer, this.vertexCount);
            }
        }
    }

    public void close() {
        if (this.vertexCount <= 0) return;

        this.vertexBuffer.scheduleFree();
        this.vertexBuffer = null;

        if (!this.autoIndexed && this.indexBuffer != null) {
            this.indexBuffer.scheduleFree();
            this.indexBuffer = null;
        }

        this.vertexCount = 0;
        this.indexCount = 0;
    }
}
