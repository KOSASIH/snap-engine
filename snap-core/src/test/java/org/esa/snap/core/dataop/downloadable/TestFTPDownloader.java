/*
 * Copyright (C) 2014 by Array Systems Computing Inc. http://www.array.ca
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */
package org.esa.snap.core.dataop.downloadable;

import org.esa.snap.core.util.SystemUtils;
import org.junit.Assume;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * FTPDownloaderTester.
 *
 * @author lveci
 */
public class TestFTPDownloader {

    @Test
    public void testConnect() throws Exception {

        boolean internetAvailable;
        try {
            URLConnection urlConnection = new URL("http://speedtest.tele2.net/").openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.getContent();
            internetAvailable = true;
        } catch (IOException e) {
            internetAvailable = false;
        }

        Assume.assumeTrue("Internet connection not available, skipping TestFTPDownloader", internetAvailable);

        final String server = "speedtest.tele2.net";
        final String remotePath = "";

        try {
            final FtpDownloader ftp = new FtpDownloader(server);
            final Map<String, Long> fileSizeMap = FtpDownloader.readRemoteFileList(ftp, server, remotePath);

            final File localFile = new File(SystemUtils.getCacheDir(), "1KB.zip");
            final String remoteFileName = localFile.getName();
            final Long fileSize = fileSizeMap.get(remoteFileName);

            Exception exception = null;
            for (int i = 0; i < 5; i++) {
                final FtpDownloader.FTPError result;
                try {
                    result = ftp.retrieveFile(remotePath + remoteFileName, localFile, fileSize);
                    if (result == FtpDownloader.FTPError.OK) {
                        localFile.delete();
                        return;
                    }
                } catch (Exception ex) {
                    exception = ex;
                }
            }
            String msg = "Not able to retrieve file";
            if (exception != null) {
                msg += " (" + exception.getMessage() + ")";
            }
            fail(msg);

        } catch (IOException e) {
            Assume.assumeNoException("Connection to Server could not be established, skipping TestFTPDownloader", e);
        }

    }

}