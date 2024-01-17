package ru.BouH.engine.game.resources.assets.shaders;

import ru.BouH.engine.game.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Shader {
    public static final String VERSION = "#version 460\n\n";
    private final List<Uniform> uniforms;
    private String shaderText;
    private final String shaderName;
    private final ShaderType shaderType;

    public Shader(ShaderType shaderType, String shaderName) {
        this.shaderType = shaderType;
        this.shaderName = shaderName;
        this.uniforms = new ArrayList<>();
        this.shaderText = "";
    }

    public void init() {
        this.shaderText = this.fillShader(this.loadStream(shaderName)).toString();
    }

    private String loadStream(String shaderName) {
        StringBuilder shaderSource = new StringBuilder();
        try (InputStream inputStream = Game.loadFileJar("shaders", shaderName + this.getShaderType().getFile())) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] subStrings = line.split(" ");
                    if (subStrings[0].equals("uniform")) {
                        String arg = subStrings[2].replace(";", "");
                        if (arg.contains("[")) {
                            String cut = arg.substring(arg.indexOf('[') + 1).replace("]", "");
                            this.getUniforms().add(new Uniform(arg.substring(0, arg.indexOf('[')), Integer.parseInt(cut)));
                        } else {
                            this.getUniforms().add(new Uniform(subStrings[2].replace(";", ""), 1));
                        }
                    }
                    shaderSource.append(line).append("\n");
                }
                reader.close();
            } else {
                throw new IOException("Couldn't read shader: " + shaderName);
            }
        } catch (IOException ex) {
            Game.getGame().getLogManager().error(ex.getMessage());
        }
        return shaderSource.toString();
    }

    private StringBuilder fillShader(String shaderStream) {
        StringBuilder shader = new StringBuilder();
        shader.append(Shader.VERSION);
        shader.append(shaderStream);
        return shader;
    }

    public List<Uniform> getUniforms() {
        return this.uniforms;
    }

    public String getShaderText() {
        return this.shaderText;
    }

    public String getShaderName() {
        return this.shaderName;
    }

    public ShaderType getShaderType() {
        return this.shaderType;
    }

    public enum ShaderType {
        FRAGMENT("/fragment.frag", ShaderType.FRAGMENT_BIT),
        VERTEX("/vertex.vert", ShaderType.VERTEX_BIT),
        GEOMETRIC("/geometric.geom", ShaderType.GEOMETRIC_BIT);
        public static final int
        FRAGMENT_BIT = (1 << 2),
        VERTEX_BIT = (1 << 3),
        GEOMETRIC_BIT = (1 << 4);

        public static final int ALL = FRAGMENT.getBit() | VERTEX.getBit() | GEOMETRIC.getBit();

        public final String file;
        private final int flag;
        ShaderType(String file, int flag) {
            this.file = file;
            this.flag = flag;
        }

        public int getBit() {
            return this.flag;
        }

        public String getFile() {
            return this.file;
        }
    }
}
