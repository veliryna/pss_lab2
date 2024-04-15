/*
Iryna Velychko, group IP-03, variant 5
А=В*МС+D*MT;
MА= min(D)*MC*ME+MZ*MT.
*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.Math;

public class Lab2 implements Callable<long[]> {

    private final float a = 1.5f;
    private int dim;
    private float[][] MC;
    private float[][] MM;
    private float[][] MZ;

    // результатні матриці
    private float[][] MP;
    private float[][] MH;
    private float[][] MF;

    //вектори
    private float[] B;
    private float[] D;

    //результатні вектори
    private float[] N;
    private float[] L;
    private float[] C;

    public static SyncWriter resultFileWriter;
    private final int func;


    public void generateInputData(int num) {
        dim = ThreadLocalRandom.current().nextInt(100, 1001);
        // generate vectors
        B = new float[dim];
        MathUtils.generateVector(B);
        D = new float[dim];
        MathUtils.generateVector(D);

        N = new float[dim];
        L = new float[dim];
        C = new float[dim];

        MC = new float[dim][dim];
        MathUtils.generateMatrix(MC);
        MM = new float[dim][dim];
        MathUtils.generateMatrix(MM);
        MZ = new float[dim][dim];
        MathUtils.generateMatrix(MZ);

        MP = new float[dim][dim];
        MH = new float[dim][dim];
        MF = new float[dim][dim];

        try {
            FileWriter fileWriter = writeInputData(num);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("generateInputData() IO exception");
        }
    }

    private FileWriter writeInputData(int num) throws IOException {
        FileWriter fileWriter = new FileWriter("input_"+ num +".txt");
        fileWriter.write(Arrays.toString(B) + "\n");
        fileWriter.write(Arrays.toString(D) + "\n");
        for (int i = 0; i < MC.length; i++) {
            fileWriter.write(Arrays.toString(MC[i]));
            if (i == MC.length - 1)
                fileWriter.write("\n");
            else
                fileWriter.write("\n");
        }
        for (int i = 0; i < MM.length; i++) {
            fileWriter.write(Arrays.toString(MM[i]));
            if (i == MM.length - 1)
                fileWriter.write("\n");
            else
                fileWriter.write("\n");
        }
        for (int i = 0; i < MZ.length; i++) {
            fileWriter.write(Arrays.toString(MZ[i]));
            if (i == MZ.length - 1)
                fileWriter.write("\n");
            else
                fileWriter.write("\n");
        }
        return fileWriter;
    }

    public void parseInputData(int num) {
        try {
            File file = new File("input_"+num+".txt");
            Scanner scanner = new Scanner(file);
            int i = 0;

            while (scanner.hasNextLine()) {
                i++;
                String data = scanner.nextLine();
                data = data.substring(1, data.length() - 1);
                String[] nums = data.split(", ");
                if (i == 1) {
                    dim = nums.length;
                    B = new float[dim];
                    D = new float[dim];
                    MC = new float[dim][dim];
                    MM = new float[dim][dim];
                    MZ = new float[dim][dim];
                    for (int j = 0; j < dim; j++) {
                        B[j] = Float.parseFloat(nums[j]);
                    }
                } else if (i == 2) {
                    for (int j = 0; j < dim; j++) {
                        D[j] = Float.parseFloat(nums[j]);
                    }
                } else if (i > 2 && i <= 2 + dim) {
                    for (int j = 0; j < dim; j++) {
                        MC[i - 3][j] = Float.parseFloat(nums[j]);
                    }
                } else if (i > 2 + dim && i <= 2 + 2 * dim) {
                    for (int j = 0; j < dim; j++) {
                        MM[i - 3 - dim][j] = Float.parseFloat(nums[j]);
                    }
                } else if (i > 2 + 2 * dim && i <= 2 + 3 * dim) {
                    for (int j = 0; j < dim; j++) {
                        MZ[i - 3 - 2 * dim][j] = Float.parseFloat(nums[j]);
                    }
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("parseInputData() IO exception");
        }
    }
    public void printMatrix(float[][] A, String info) {
        Thread t = Thread.currentThread();
        StringBuilder matrix = new StringBuilder("\nThread: " + t.getName() + ", " + info + "\n");
        for (float[] floats : A) {
            matrix.append(Arrays.toString(floats)).append("\n");
        }
        System.out.println(matrix);
        resultFileWriter.write(matrix.toString());
    }

    public void printVector(float[] A, String info) {
        Thread t = Thread.currentThread();
        StringBuilder vector = new StringBuilder("\nThread: " + t.getName() + ", " + info + "\n");
        vector.append(Arrays.toString(A)).append("\n");
        System.out.println(vector);
        resultFileWriter.write(vector.toString());
    }

    public void Function1() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(4);
        List<Future<Boolean>> futureResults = new ArrayList<>();

        int n = (int)Math.ceil(MC.length/2);
        Callable<Boolean> c1 = () -> {
            MathUtils.multiplyMatrices( B, MC, N, 0, n);
            return true;
        };
        Callable<Boolean> c2 = () -> {
            MathUtils.multiplyMatrices( B, MC, N, n, MC.length);
            return true;
        };
        Callable<Boolean> c3 = () -> {
            MathUtils.multiplyMatrices( D, MM, L, 0, D.length);
            MathUtils.multiplyMatrixByValue(L, -1);
            return true;
        };
        futureResults.add(service.submit(c1));
        futureResults.add(service.submit(c2));
        futureResults.add(service.submit(c3));

        for(int i = 0; i< futureResults.size(); i++) {
            futureResults.get(i).get();
        }
        service.shutdown();

        MathUtils.addMatrices(N, L, C);
    }

    public void Function2() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(4);
        List<Future<Boolean>> futureResults = new ArrayList<>();


        Callable<Boolean> c1 = () -> {
            int n = (int)Math.ceil(MC.length/2);
            float[][] T = new float[1][dim];
            MathUtils.addMatrices(B, D, T);
            float minValue = MathUtils.findMinValue(T[0]);
            MathUtils.multiplyMatrixByValue(MZ, minValue);
            ExecutorService service1 = Executors.newFixedThreadPool(4);

            Callable<Boolean> c = () -> {
                MathUtils.multiplyMatrices( MC, MZ, MP, 0, n);
                return true;
            };

            Callable<Boolean> cn = () -> {
                MathUtils.multiplyMatrices( MC, MZ, MP, n, MC.length);
                return true;
            };

            Future<Boolean> f1 = service1.submit(c);
            Future<Boolean> f2 = service1.submit(cn);

            f1.get();
            f2.get();
            service1.shutdown();

            return true;
        };

        Callable<Boolean> c2 = () -> {
            int n = (int)Math.ceil(MM.length/2);
            float[][] T = new float[dim][dim];
            MathUtils.addMatrices(MC, MM, T);
            MathUtils.multiplyMatrixByValue(T, a);
            ExecutorService service1 = Executors.newFixedThreadPool(4);

            Callable<Boolean> c = () -> {
                MathUtils.multiplyMatrices( MM, T, MH, 0, n);
                return true;
            };

            Callable<Boolean> cn = () -> {
                MathUtils.multiplyMatrices( MM, T, MH, n, MM.length);
                return true;
            };

            Future<Boolean> f1 = service1.submit(c);
            Future<Boolean> f2 = service1.submit(cn);

            f1.get();
            f2.get();
            service1.shutdown();

            return true;
        };


        futureResults.add(service.submit(c1));
        futureResults.add(service.submit(c2));

        for(int i = 0; i< futureResults.size(); i++) {
            futureResults.get(i).get();
        }
        service.shutdown();

        MathUtils.addMatrices(MP, MH, MF);
    }

    public long[] call() throws InterruptedException, ExecutionException{
        float[][] output;
        String name;
        if (func == 1) {
            Function1();
            output = C;
            name = "C";
        } else {
            Function2();
            output = MF;
            name = "MF";
        }
        long startTime = System.nanoTime();
        long estimatedTime = System.nanoTime() - startTime;
        printMatrix(output, name, estimatedTime);
        return new long[] {dim, estimatedTime};
    }

    public Lab2(int i, int iter) {
        func = i;
        parseInputData(iter);
        N = new float[1][dim];
        L = new float[1][dim];
        C = new float[1][dim];
        MP = new float[dim][dim];
        MH = new float[dim][dim];
        MF = new float[dim][dim];
    }

    public static void main(String[] args) {
        int times = 50;
        int checks = 5;
        Lab2.resultFileWriter = new SyncWriter("result.txt");

        // generate input datas
        // Lab2 lab2 = new Lab2(0, 0);
        // for (int i = 0; i < Math.abs(times/checks); i++) {
        //   lab2.generateInputData(i);
        // }

        long[][] timing = new long[times][2];
        long[][] timing2 = new long[times][2];

        ExecutorService service = Executors.newFixedThreadPool(times);
        List<Future<long[]>> futureResults = new ArrayList<>();
        List<Future<long[]>> futureResults2 = new ArrayList<>();

        for (int i = 0; i < times; i++) {
            int iter = (int)Math.floor(i/checks);
            System.out.println("Use input "+iter);

            Future<long[]> future = service.submit(new Lab2(1, iter));
            futureResults.add(future);

            Future<long[]> future2 = service.submit(new Lab2(2, iter));
            futureResults2.add(future2);


        }
        for (int i = 0; i < times; i++) {
            Future<long[]> future = futureResults.get(i);
            Future<long[]> future2 = futureResults2.get(i);
            try {
                long[] res = future.get();
                timing[i][0] = res[0];
                timing[i][1] = res[1];

                long[] res2 = future2.get();
                timing2[i][0] = res2[0];
                timing2[i][1] = res2[1];
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        service.shutdown();
        Arrays.sort(timing, Comparator.comparingDouble(o -> o[0]));
        Arrays.sort(timing2, Comparator.comparingDouble(o -> o[0]));

        String x = "";
        String y = "";
        String x2 = "";
        String y2 = "";
        float[] matrixN = new float[times/checks];
        float[] middleValues = new float[times/checks];
        float[] middleValues2 = new float[times/checks];
        Arrays.fill(middleValues, 0.0f);
        for (int i = 0; i < timing2.length; i++) {
            matrixN[(int)Math.floor(i/checks)] = timing[i][0];
            middleValues[(int)Math.floor(i/checks)] += timing[i][1];
            middleValues2[(int)Math.floor(i/checks)] += timing2[i][1];
        }

        for (int i = 0; i < middleValues.length; ++ i) {
            middleValues[i] = middleValues[i]/checks;
            middleValues2[i] = middleValues2[i]/checks;
            x += matrixN[i] + ", ";
            x2 += matrixN[i] + ", ";
            y += middleValues[i] + ", ";
            y2 += middleValues2[i] + ", ";
        }

        Lab2.resultFileWriter.write("x: " + x.substring(0, x.length() - 2) + "\n");
        Lab2.resultFileWriter.write("y: " + y.substring(0, y.length() - 2) + "\n\n");
        Lab2.resultFileWriter.write("x2: " + x2.substring(0, x2.length() - 2) + "\n");
        Lab2.resultFileWriter.write("y2: " + y2.substring(0, y2.length() - 2));
    }

}