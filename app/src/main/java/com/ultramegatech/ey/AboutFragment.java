/*
 * The MIT License (MIT)
 * Copyright © 2012 Steve Guidetti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ultramegatech.ey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Dialog that shows information about the application.
 *
 * @author Steve Guidetti
 */
public class AboutFragment extends DialogFragment {
    @NonNull
    private static final String TAG = "AboutDialog";

    /**
     * Show the Dialog.
     *
     * @param fm The FragmentManager
     */
    public static void showDialog(@NonNull FragmentManager fm) {
        final DialogFragment fragment = new AboutFragment();
        fragment.show(fm, TAG);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_about, null, false);

        ((TextView)root.findViewById(R.id.version)).setText(BuildConfig.VERSION_NAME);
        root.findViewById(R.id.support).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });
        root.findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite();
            }
        });
        root.findViewById(R.id.license).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLicense();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.titleAbout);
    }

    /**
     * Open an email client to send a support request.
     */
    private void openEmail() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.aboutEmail)});
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.aboutEmailSubject, BuildConfig.VERSION_NAME));
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.aboutSendEmail)));
        } else {
            Toast.makeText(getContext(), R.string.errorNoEmail, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Open the Elementary GitHub website in a browser.
     */
    private void openWebsite() {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.aboutWebsiteUrl)));
        startActivity(intent);
    }

    /**
     * Open the software license in a browser.
     */
    private void openLicense() {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.aboutLicenseUrl)));
        startActivity(intent);
    }
}
