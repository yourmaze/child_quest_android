package com.minus30.childquest;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

class Decompress extends AsyncTask<Void, Integer, Void> {

    private String zipFileName;
    private String zipLocation;
    private int per = 0;
    private ProgressBar bar;

    public Decompress(String zipFile, String location, ProgressBar bars) {
        bar = bars;
        zipFileName = zipFile;
        zipLocation = location;
        _dirChecker("");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            ZipFile zip = new ZipFile(zipFileName);

            Log.d("Decompress", "zip" + zip);
            bar.setMax(zip.size());
            FileInputStream fin = new FileInputStream(zipFileName);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;

            while ((ze = zin.getNextEntry()) != null) {

                Log.d("Decompress", "Unzipping " + ze.getName());
                if (ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    // Here I am doing the update of my progress bar
                    per++;
                    publishProgress(per);

                    FileOutputStream fout = new FileOutputStream(zipLocation + ze.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);

                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = zin.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }
                    zin.closeEntry();
                    bufout.close();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            Log.e("Decompress", "Error", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d("Decompress", "Completed." + result);
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        bar.setProgress(per); //Since it's an inner class, Bar should be able to be called directly
    }

    private void _dirChecker(String dir) {
        File f = new File(zipLocation + dir);
        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }

}