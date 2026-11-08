import java.io.*;

class CopyFiles {

    static void copyFile(String from, String to) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(from)));
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(to)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    static void createTestFile(String name, int lines) throws IOException {
        File f = new File(name);
        if (f.exists()) return;
        try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
            for (int i = 1; i <= lines; i++) {
                w.write("Строка номер " + i + " текстового файла " + name);
                w.newLine();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String in1 = "data_alpha.txt", in2 = "data_beta.txt";
        String out1 = "copy_seq_alpha.txt", out2 = "copy_seq_beta.txt";
        String out3 = "copy_par_alpha.txt", out4 = "copy_par_beta.txt";

        createTestFile(in1, 2_000_000);
        createTestFile(in2, 2_000_000);

        long startSeq = System.nanoTime();
        copyFile(in1, out1);
        copyFile(in2, out2);
        long seqTime = System.nanoTime() - startSeq;

        Thread t1 = new Thread(() -> {
            try {
                copyFile(in1, out3);
            } catch (IOException e) {
                System.out.println("Ошибка копирования " + in1 + ": " + e.getMessage());
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                copyFile(in2, out4);
            } catch (IOException e) {
                System.out.println("Ошибка копирования " + in2 + ": " + e.getMessage());
            }
        });

        long startPar = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long parTime = System.nanoTime() - startPar;

        System.out.println("Последовательно: " + seqTime / 1_000_000.0 + " мс");
        System.out.println("Параллельно:     " + parTime / 1_000_000.0 + " мс");
        System.out.println(parTime < seqTime ? "Параллельное копирование быстрее" : "Последовательное копирование быстрее");
    }
}
