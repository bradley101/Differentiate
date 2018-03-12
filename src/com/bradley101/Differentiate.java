package com.bradley101;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Differentiate {
    private File file;
    private int CHUNK_SIZE = 1024 * 1024;

    public Differentiate(File file) {
        this.file = file;
    }

    public Differentiate(File file, int chunkSize) {
        this.file = file;
        this.CHUNK_SIZE = chunkSize;
    }

    public File getNthFileChunk(int n) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek((n-1) * CHUNK_SIZE);
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesAmount = randomAccessFile.read(buffer);
            File f = new File(file.getParent(), file.getName() + "." + Integer.valueOf(n));
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(buffer, 0, bytesAmount);
            fileOutputStream.close();
            return f;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<File> getDifferentiatedChunks() {
        List<File> chunkList = new ArrayList<>();
        chunkList = differentiate();
        return chunkList;
    }

    public File getIntegratedfile(List<File> fileChunkList, File finalFile) {
        File outputfile = new File(finalFile.getParent(), finalFile.getName());
        try(FileOutputStream fileOutputStream = new FileOutputStream(outputfile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);) {

            for (File f : fileChunkList) {
                Files.copy(f.toPath(), bufferedOutputStream);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return outputfile;
    }

    private List<File> differentiate() {
        List<File> list = new ArrayList<>();
        byte[] buffer = new byte[CHUNK_SIZE];
        String fileName = file.getName();
        int partCounter = 1;

        try(FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            int bytesAmount = 0;
            while ((bytesAmount = bufferedInputStream.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                String filePartName = String.format("%s.%03d", fileName, partCounter++);
                File newFile = new File(file.getParent(), filePartName);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, bytesAmount);
                }
                list.add(newFile);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public static void main(String[] args) {

        File f = new File(System.getProperty("user.home")+"/Desktop", "video.mp4");

        Differentiate differentiate = new Differentiate(f, 2 * 1024 * 1024);

        System.out.println(differentiate.getNthFileChunk(1));

        //System.out.println(differentiate.getIntegratedfile(differentiate.getDifferentiatedChunks(), new File(f.getParent(), "newvideo.mp4")));
    }
}
