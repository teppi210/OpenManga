package org.nv95.openmanga.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.nv95.openmanga.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by nv95 on 16.10.15.
 */
public class ErrorReporter {
    private Context context;
    private FileOutputStream ostream;

    public ErrorReporter(Context context) {
        this.context = context;
        File file = new File(context.getExternalFilesDir("debug"), "log.txt");
        try {
             ostream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void report(String msg) {
        try {
            msg += "\n ---- \n";
            ostream.write(msg.getBytes());
            ostream.flush();
            Toast.makeText(context, R.string.exception_logged, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void report(Exception e) {
        report(e.getLocalizedMessage());
    }

    @Override
    protected void finalize() throws Throwable {
        ostream.close();
        super.finalize();
    }

    public static void sendLog(final Context context){
        new AlertDialog.Builder(context).setTitle(R.string.bug_report)
                .setMessage(R.string.bug_report_message)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(context.getExternalFilesDir("debug"), "log.txt");
                        if (!file.exists()) {
                            Toast.makeText(context, R.string.log_not_found, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
                                {"nvasya95@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                "Error report for OpenManga");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.bug_report)));
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }
}
