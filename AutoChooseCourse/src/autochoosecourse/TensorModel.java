package autochoosecourse;

import java.io.File;
import javax.swing.JOptionPane;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import org.tensorflow.DataType;
import org.tensorflow.Output;
import org.tensorflow.types.UInt8;

public class TensorModel {

    private byte[] graphDef;
    private static String[] labels = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private String MainPath = "";

    public TensorModel(String path) {
        this.MainPath = path;

    }

    public String getVerificationCode() throws Exception {
        File fileOut = null;
        int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        if (bitness == 32) {
            fileOut = new File(MainPath + "/lib/x86/opencv_java347.dll");
        } else if (bitness == 64) {
            fileOut = new File(MainPath + "/lib/x64/opencv_java347.dll");
        }
        System.load(fileOut.toString());
            //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Instantiating the Imagecodecs class 
        Imgcodecs imageCodecs = new Imgcodecs();

        //Reading the Image from the file  
        Mat image_source = imageCodecs.imread(MainPath + "/image.png");

        //Reading pb file as bytes
        graphDef = readAllBytesOrExist(Paths.get(MainPath + "/code_model.pb"));
        //JOptionPane.showMessageDialog(null, graphDef.toString());

        //Resize the Image 
        Mat resizeimage = new Mat();
        Size sz = new Size(170, 80);
        Imgproc.resize(image_source, resizeimage, sz, 0, 0, Imgproc.INTER_CUBIC);

        //Convert the Image to grayscale 
        Mat gray = new Mat();
        Imgproc.cvtColor(resizeimage, gray, Imgproc.COLOR_RGB2GRAY);

        //Convert the Image to binary
        Mat im_bw = new Mat();
        Imgproc.threshold(gray, im_bw, 128, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        // Saving Image 
        Imgcodecs.imwrite(MainPath + "/image_java_generated.png", im_bw);

        //Reading image file as bytes
        byte[] imageBytes = readAllBytesOrExist(Paths.get(MainPath + "/image_java_generated.png"));

        //java tensorflow input pipeline
        try (Tensor<Float> image = constructAndExecuteGraph(imageBytes)) {
            // interface through the CNN model and returns 4 code result
            float[][][] labelProbabilities = executeCNNGraph(graphDef, image);
            String str = "";
            for (int i = 0; i < 4; i++) {
                int bestLabelIdx = maxIndex(labelProbabilities[i][0]);
                System.out.println(String.format("BEST MATCH: %s (%.2f%% likely)", labels[bestLabelIdx], labelProbabilities[i][0][bestLabelIdx] * 100f));
                str = str + labels[bestLabelIdx];
            }
            //JOptionPane.showMessageDialog(null, str);
            return str;
        }
            //JOptionPane.showMessageDialog(null, "Image Loaded");

        //return null;
    }

    private int maxIndex(float[] probabilities) throws Exception{
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

    private float[][][] executeCNNGraph(byte[] graphDef, Tensor<Float> image) throws Exception{
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g); // Generally, there may be multiple output tensors, all of them must be closed to prevent resource leaks. 
                    Tensor<Float> result_digit1 = s.runner().feed("input_1", image).fetch("digit1/Softmax").run().get(0).expect(Float.class);
                    Tensor<Float> result_digit2 = s.runner().feed("input_1", image).fetch("digit2/Softmax").run().get(0).expect(Float.class);
                    Tensor<Float> result_digit3 = s.runner().feed("input_1", image).fetch("digit3/Softmax").run().get(0).expect(Float.class);
                    Tensor<Float> result_digit4 = s.runner().feed("input_1", image).fetch("digit4/Softmax").run().get(0).expect(Float.class);) {

                final long[] rshape_digit1 = result_digit1.shape();
                final long[] rshape_digit2 = result_digit2.shape();
                final long[] rshape_digit3 = result_digit3.shape();
                final long[] rshape_digit4 = result_digit4.shape();

                int nlabels = (int) rshape_digit1[1]; //36
                float[][][] result = new float[4][1][nlabels];
                result_digit1.copyTo(result[0]);
                result_digit2.copyTo(result[1]);
                result_digit3.copyTo(result[2]);
                result_digit4.copyTo(result[3]);
                System.out.println(result[1][0].length);
                return result;
            }
        }

    }

    private Tensor<Float> constructAndExecuteGraph(byte[] imageBytes) throws Exception{
        try (Graph g = new Graph()) {
            GraphBuilder b = new GraphBuilder(g);
            final Output<String> input = b.constant("input", imageBytes);
            final Output<Float> output = b.expandDims(b.cast(b.decodeJpeg(input, 1), Float.class), b.constant("make_batch", 0));
            try (Session s = new Session(g)) {
                return s.runner().fetch(output.op().name()).run().get(0).expect(Float.class);
            }
        }

    }

    private byte[] readAllBytesOrExist(Path path) throws Exception{
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            throw e;
        }
    }

    class GraphBuilder {

        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output<Float> div(Output<Float> x, Output<Float> y) {
            return binaryOp("Div", x, y);
        }

        <T> Output<T> sub(Output<T> x, Output<T> y) {
            return binaryOp("Sub", x, y);
        }

        <T> Output<Float> resizeBilinear(Output<T> images, Output<Integer> size) {
            return binaryOp3("ResizeBilinear", images, size);
        }

        <T> Output<T> expandDims(Output<T> input, Output<Integer> dim) {
            return binaryOp3("ExpandDims", input, dim);
        }

        <T, U> Output<U> cast(Output<T> value, Class<U> type) {
            DataType dtype = DataType.fromClass(type);
            return g.opBuilder("Cast", "Cast")
                    .addInput(value)
                    .setAttr("DstT", dtype)
                    .build()
                    .<U>output(0);
        }

        Output<UInt8> decodeJpeg(Output<String> contents, long channels) {
            return g.opBuilder("DecodeJpeg", "DecodeJpeg")
                    .addInput(contents)
                    .setAttr("channels", channels)
                    .build()
                    .<UInt8>output(0);
        }

        <T> Output<T> constant(String name, Object value, Class<T> type) {
            try (Tensor<T> t = Tensor.<T>create(value, type)) {
                return g.opBuilder("Const", name)
                        .setAttr("dtype", DataType.fromClass(type))
                        .setAttr("value", t)
                        .build()
                        .<T>output(0);
            }
        }

        Output<String> constant(String name, byte[] value) {
            return this.constant(name, value, String.class);
        }

        Output<Integer> constant(String name, int value) {
            return this.constant(name, value, Integer.class);
        }

        Output<Integer> constant(String name, int[] value) {
            return this.constant(name, value, Integer.class);
        }

        Output<Float> constant(String name, float value) {
            return this.constant(name, value, Float.class);
        }

        private <T> Output<T> binaryOp(String type, Output<T> in1, Output<T> in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
        }

        private <T, U, V> Output<T> binaryOp3(String type, Output<U> in1, Output<V> in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
        }
        private Graph g;
    }
}
