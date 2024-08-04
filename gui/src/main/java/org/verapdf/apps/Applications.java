/**
 * This file is part of VeraPDF Library GUI, a module of the veraPDF project.
 * Copyright (c) 2015-2024, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * VeraPDF Library GUI is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with VeraPDF Library GUI as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * VeraPDF Library GUI as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.verapdf.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.verapdf.ReleaseDetails;
import org.verapdf.processor.app.AppConfigBuilder;
import org.verapdf.processor.app.ConfigManager;
import org.verapdf.processor.app.ConfigManagerImpl;
import org.verapdf.processor.app.VeraAppConfig;
import org.verapdf.processor.app.VeraAppConfigImpl;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 31 Oct 2016:13:15:23
 */

public final class Applications {
	public static final String APP_HOME_PROPERTY = "app.home"; //$NON-NLS-1$
	public static final String DEFAULT_CONFIG_ROOT_NAME = "config"; //$NON-NLS-1$
	private static final String write_io_message = "IOException trying to write %s directory.";
	private static final String not_writable_message = "Arg root:%s must be a writable directory.";

	public static final String UPDATE_SERVICE_NOT_AVAILABLE = "Update Service not available"; //$NON-NLS-1$
	public static final String UPDATE_LATEST_VERSION = "You are currently running the latest version of veraPDF%s v%s"; //$NON-NLS-1$
	public static final String UPDATE_OLD_VERSION = "You are NOT running the latest version of veraPDF.\nYou are running version %s, the latest version is %s.\n"; //$NON-NLS-1$
	public static final String UPDATE_URI = "http://downloads.verapdf.org/rel/verapdf-installer.zip"; //$NON-NLS-1$
	
	private static final String VERAPDF = "verapdf";
	private static final String VERAPDF_WITH_BAT = VERAPDF + ".bat";

	private static final String APPDATA_NAME = "APPDATA";
	private static final String USER_HOME_PROPERTY = "user.home";
	private static final String DOT = ".";

	private Applications() {
		assert (false);
	}
	
	public static File getVeraScriptFile() {
		File startFile = null;
		String appHome = System.getProperty(APP_HOME_PROPERTY);
		
		if (appHome != null) {
			File veraPdfDirectory = new File(appHome);
			startFile = getStartFile(veraPdfDirectory);
		}
		return startFile;
	}

	private static File getStartFile(File veraPdfDirectory) {
		File unixStartFile = new File(veraPdfDirectory, VERAPDF);
		if (unixStartFile.isFile()) {
			return unixStartFile;
		}
		File windowsStartFile = new File(veraPdfDirectory, VERAPDF_WITH_BAT);
		if (windowsStartFile.isFile()) {
			return windowsStartFile;
		}
		return null;
	}

	/**
	 * @return the Application Release details for the GUI
	 */
	public static ReleaseDetails getAppDetails() {
		return ReleaseDetails.byId("gui"); //$NON-NLS-1$
	}

	/**
	 * @param root
	 *            the root directory for the configuration, should contain the
	 *            veraPDF configuration files, if not default ones are created.
	 * @return a {@link ConfigManager} instance populated using the
	 *         configuration files at root.
	 */
	public static ConfigManager createConfigManager(final File root) {
		if (root == null)
			throw new NullPointerException("Arg root cannot be null");
		if ((!root.isDirectory() && !root.mkdir()) || !Files.isWritable(root.toPath())) {
			throw new IllegalArgumentException(String.format(not_writable_message, root.getAbsolutePath()));
		}
		return ConfigManagerImpl.create(root);
	}

	/**
	 * Shortcut method to find configuration in the local user directory or,
	 * if it doesn't exist, to create configuration in the application
	 * installation configuration directory,
	 * if installation configuration directory is not writable method shall
	 * create configuration in the local user directory.
	 * 
	 * @return a {@link ConfigManager} instance populated using the
	 *         configuration files in the application config directory.
	 */
	public static ConfigManager createAppConfigManager() {
		File configRoot = new File("");
		try {
			configRoot = configRoot();
			return createConfigManager(configRoot);
		} catch (IOException excep) {
			throw new IllegalStateException(String.format(write_io_message, configRoot.getAbsolutePath()), excep);
		}
	}

	/**
	 * Shortcut method to create a configuration in the temp directory.
	 * 
	 * @return a {@link ConfigManager} instance populated using the
	 *         configuration files in the temp directory.
	 */
	public static ConfigManager createTmpConfigManager() {
		try {
			return createConfigManager(tempRoot());
		} catch (IOException excep) {
			throw new IllegalStateException(String.format(write_io_message, "temp"), excep); //$NON-NLS-1$
		}
	}

	public static VeraAppConfig defaultConfig() {
		return VeraAppConfigImpl.defaultInstance();
	}

	public static AppConfigBuilder defaultConfigBuilder() {
		return AppConfigBuilder.defaultBuilder();
	}

	public static AppConfigBuilder createConfigBuilder(VeraAppConfig base) {
		return AppConfigBuilder.fromConfig(base);
	}

	public static SoftwareUpdater softwareUpdater() {
		return new SoftwareUpdaterImpl();
	}

	public static void checkArgNotNull(final Object arg, final String argName) {
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Parameter %s can not be null", argName)); //$NON-NLS-1$
		}
	}

	private static File configRoot() throws IOException {
		String path = System.getenv(APPDATA_NAME);
		if (path == null) {
			path = System.getProperty(USER_HOME_PROPERTY) + File.separator + DOT;
		} else {
			path += File.separator;
		}
		path += VERAPDF + File.separator + DEFAULT_CONFIG_ROOT_NAME;
		File localRoot = new File(path);
		if (localRoot.exists() && areDirectoryFilesWritable(localRoot)) {
			return localRoot;
		}
		File appHomeRoot = appHomeRoot();
		if (appHomeRoot != null && Files.isWritable(appHomeRoot.toPath()) && areDirectoryFilesWritable(appHomeRoot)) {
			return appHomeRoot;
		}
		if (localRoot.mkdirs() && Files.isWritable(localRoot.toPath())) {
			return localRoot;
		}
		return tempRoot();
	}

	private static File appHomeRoot() {
		String appHome = System.getProperty(APP_HOME_PROPERTY);
		if (appHome != null) {
			File user = new File(appHome);
			File f = new File(user, DEFAULT_CONFIG_ROOT_NAME);
			if (f.exists() || f.mkdir()) {
				return f;
			}
		}
		return null;
	}

	private static File tempRoot() throws IOException {
		File temp = Files.createTempDirectory("").toFile(); //$NON-NLS-1$
		temp.deleteOnExit();
		return temp;
	}

	private static boolean areDirectoryFilesWritable(File directory) {
		String[] files = directory.list();
		if (files == null) {
			return Files.isWritable(directory.toPath());
		}
		for (String fileName : files) {
			if (!Files.isWritable(new File(directory + File.separator + fileName).toPath())) {
				return false;
			}
		}
		return true;
	}
}
