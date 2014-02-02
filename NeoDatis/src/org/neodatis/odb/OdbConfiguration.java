/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.neodatis.odb.core.ICoreProvider;
import org.neodatis.odb.core.NeoDatisError;
import org.neodatis.odb.core.query.execution.IQueryExecutorCallback;
import org.neodatis.odb.impl.DefaultCoreProvider;
import org.neodatis.odb.impl.core.layers.layer3.engine.DefaultByteArrayConverter;
import org.neodatis.odb.impl.core.layers.layer3.engine.StorageEngineConstant;
import org.neodatis.odb.impl.core.server.layers.layer3.engine.DefaultMessageStreamer;
import org.neodatis.tool.wrappers.ConstantWrapper;
import org.neodatis.tool.wrappers.NeoDatisClassLoader;
import org.neodatis.tool.wrappers.io.OdbFileIO;
import org.neodatis.tool.wrappers.map.OdbHashMap;

/**
 * The main NeoDatis ODB Configuration class. All engine configuration is done
 * via this class.
 * 
 * @author osmadja
 * 
 */
public class OdbConfiguration {

	private static boolean coreProviderInit = false;
	private static boolean debugEnabled = false;
	private static boolean logAll = false;
	private static int debugLevel = 100;
	private static Map<String, String> logIds = null;
	private static boolean infoEnabled = false;
	private static boolean enableAfterWriteChecking = false;
	private static int maxNumberOfWriteObjectPerTransaction = 10000;
	private static long maxNumberOfObjectInCache = 3000000;
	private static int defaultBufferSizeForData = 1024 * 4;
	private static int defaultBufferSizeForTransaction = 4096 * 4;
	private static int nbBuffers = 5;
	private static boolean useMultiBuffer = true;
	private static boolean automaticCloseFileOnExit = false;
	private static boolean saveHistory = false;

	private static String defaultDatabaseCharacterEncoding = "ISO8859-1";
	private static String databaseCharacterEncoding = defaultDatabaseCharacterEncoding;

	private static boolean throwExceptionWhenInconsistencyFound = true;
	private static final int NB_IDS_PER_BLOCK = 1000;
	private static final int ID_BLOCK_REPETITION_SIZE = 18;
	/** header(34) + 1000 * 18 */
	private static int idBlockSize = 34 + NB_IDS_PER_BLOCK * ID_BLOCK_REPETITION_SIZE;
	private static boolean inPlaceUpdate = false;
	private static int stringSpaceReserveFactor = 1;
	private static boolean checkModelCompatibility = true;
	private static boolean monitorMemory = false;

	/**
	 * A boolean value to indicate if ODB can create empty constructor when not
	 * available
	 */
	private static boolean enableEmptyConstructorCreation = true;

	// For multi thread
	/**
	 * a boolean value to specify if ODBFactory waits a little to re-open a file
	 * when a file is locked
	 */
	private static boolean retryIfFileIsLocked = false;
	/** How many times ODBFactory tries to open the file when it is locked */
	private static int numberOfRetryToOpenFile = 5;
	/** How much time (in ms) ODBFactory waits between each retry */
	private static long retryTimeout = 100;
	private static long timeoutToAcquireMutexInMultiThread = 60000;
	/** neodatiesee : when true, the mutex is only released on commit or close, which would prevent another thread acessing the db before commit.
	 * if false, the mutex is released after the getObjects, store and delete
	 * 
	 */
	private static boolean multiThreadExclusive = true;

	/**
	 * How much time (in ms) ODBFactory waits to be sure a file has been created
	 */
	private static long defaultFileCreationTime = 500;

	/** Automatically increase cache size when it is full */
	private static boolean automaticallyIncreaseCacheSize = false;

	private static boolean useCache = true;

	private static boolean logServerStartupAndShutdown = true;

	private static boolean logServerConnections = false;

	/** The default btree size for index btrees */
	private static int defaultIndexBTreeDegree = 20;

	/**
	 * The type of cache. If true, the cache use weak references that allows
	 * very big inserts,selects like a million of objects. But it is a little
	 * bit slower than setting to false
	 */
	private static boolean useLazyCache = false;

	/** To indicate if warning must be displayed */
	private static boolean displayWarnings = true;

	private static IQueryExecutorCallback queryExecutorCallback = null;

	/** Scale used for average action * */
	private static int scaleForAverageDivision = 2;

	/** Round Type used for the average division */
	private static int roundTypeForAverageDivision = ConstantWrapper.ROUND_TYPE_FOR_AVERAGE_DIVISION;

	/** for IO atomic writing&reading */
	private static Class ioClass = OdbFileIO.class;
	/** for IO atomic : password for encryption */
	private static String encryptionPassword;

	/** The core provider is the provider of core object implementation for ODB */
	private static ICoreProvider coreProvider = new DefaultCoreProvider();

	/** To indicate if NeoDatis must check the runtime version, defaults to yes */
	private static boolean checkRuntimeVersion = true;

	/**
	 * To specify if NeoDatis must automatically reconnect objects loaded in previous
	 * session. With with flag on, user does not need to manually reconnect an
	 * object. Default value = true
	 */
	private static boolean reconnectObjectsToSession = false;

	private static ClassLoader classLoader = NeoDatisClassLoader.getCurrent();
	private static Class messageStreamerClass = DefaultMessageStreamer.class;

	/**
	 * To activate or desactivate the use of index
	 * 
	 */
	private static boolean useIndex = true;
	
	/** Used to let same vm mode use an odb connection on severals different threads
	 * 
	 */
	private static boolean shareSameVmConnectionMultiThread = true;
	private static boolean lockObjectsOnSelect = false;
	
	/** a DatabaseStartupManager is called on every database open*/
	private static DatabaseStartupManager databaseStartupManager = null;
	
	
	
	/**
	 * @return
	 */
	public static boolean reconnectObjectsToSession() {
		return reconnectObjectsToSession;
	}
	
	/**@deprecated Use OdbConfiguration.setReconnectObjectsToSession instead
	 * 
	 * @param autoReconnectObjectsToSession
	 */
	public static void setAutoReconnectObjectsToSession(boolean autoReconnectObjectsToSession) {
		OdbConfiguration.reconnectObjectsToSession = autoReconnectObjectsToSession;
	}

	public static void setReconnectObjectsToSession(boolean reconnectObjectsToSession) {
		OdbConfiguration.reconnectObjectsToSession = reconnectObjectsToSession;
	}


	public static int getDefaultBufferSizeForData() {
		return defaultBufferSizeForData;
	}

	public static void setDefaultBufferSizeForData(int defaultBufferSize) {
		OdbConfiguration.defaultBufferSizeForData = defaultBufferSize;
	}

	public static void addLogId(String logId) {
		if (logIds == null) {
			logIds = new OdbHashMap<String, String>();
		}
		logIds.put(logId, logId);
	}

	public static void removeLogId(String logId) {
		if (logIds == null) {
			logIds = new OdbHashMap<String, String>();
		}
		logIds.remove(logId);
	}

	public static boolean isDebugEnabled(String logId) {
		if(!debugEnabled){
			return false;
		}
		if (logAll) {
			return true;
		}

		if (logIds == null || logIds.size()==0) {
			return false;
		}

		return logIds.containsKey(logId);
	}

	public static void setDebugEnabled(int level, boolean debug) {
		OdbConfiguration.debugEnabled = debug;
		OdbConfiguration.debugLevel = level;
	}

	public static boolean isEnableAfterWriteChecking() {
		return enableAfterWriteChecking;
	}

	public static boolean isInfoEnabled() {
		return infoEnabled;
	}

	public static boolean isInfoEnabled(String logId) {
		// return false;

		if (logAll) {
			return true;
		}

		if (logIds == null || logIds.size()==0) {
			return false;
		}

		return logIds.containsKey(logId);

		// return false;
	}

	public static void setInfoEnabled(boolean infoEnabled) {
		OdbConfiguration.infoEnabled = infoEnabled;
	}

	public static void setEnableAfterWriteChecking(boolean enableAfterWriteChecking) {
		OdbConfiguration.enableAfterWriteChecking = enableAfterWriteChecking;
	}

	public static int getMaxNumberOfWriteObjectPerTransaction() {
		return maxNumberOfWriteObjectPerTransaction;
	}

	public static void setMaxNumberOfWriteObjectPerTransaction(int maxNumberOfWriteObjectPerTransaction) {
		OdbConfiguration.maxNumberOfWriteObjectPerTransaction = maxNumberOfWriteObjectPerTransaction;
	}

	public static long getMaxNumberOfObjectInCache() {
		return maxNumberOfObjectInCache;
	}

	public static void setMaxNumberOfObjectInCache(long maxNumberOfObjectInCache) {
		OdbConfiguration.maxNumberOfObjectInCache = maxNumberOfObjectInCache;
	}

	public static int getNumberOfRetryToOpenFile() {
		return numberOfRetryToOpenFile;
	}

	public static void setNumberOfRetryToOpenFile(int numberOfRetryToOpenFile) {
		OdbConfiguration.numberOfRetryToOpenFile = numberOfRetryToOpenFile;
	}

	public static long getRetryTimeout() {
		return retryTimeout;
	}

	public static void setRetryTimeout(long retryTimeout) {
		OdbConfiguration.retryTimeout = retryTimeout;
	}

	public static boolean retryIfFileIsLocked() {
		return retryIfFileIsLocked;
	}

	public static void setRetryIfFileIsLocked(boolean retryIfFileIsLocked) {
		OdbConfiguration.retryIfFileIsLocked = retryIfFileIsLocked;
	}

	public static long getDefaultFileCreationTime() {
		return defaultFileCreationTime;
	}

	public static void setDefaultFileCreationTime(long defaultFileCreationTime) {
		OdbConfiguration.defaultFileCreationTime = defaultFileCreationTime;
	}

	public static boolean isMultiThread() {
		return retryIfFileIsLocked;
	}

	public static void useMultiThread(boolean yes) {
		useMultiThread(yes, numberOfRetryToOpenFile);
	}

	public static void useMultiThread(boolean yes, int numberOfThreads) {
		setRetryIfFileIsLocked(yes);
		if (yes) {
			setNumberOfRetryToOpenFile(numberOfThreads * 10);
			setRetryTimeout(50);
		}
	}

	public static boolean throwExceptionWhenInconsistencyFound() {
		return throwExceptionWhenInconsistencyFound;
	}

	public static void setThrowExceptionWhenInconsistencyFound(boolean throwExceptionWhenInconsistencyFound) {
		OdbConfiguration.throwExceptionWhenInconsistencyFound = throwExceptionWhenInconsistencyFound;
	}

	public static boolean automaticallyIncreaseCacheSize() {
		return automaticallyIncreaseCacheSize;
	}

	public static void setAutomaticallyIncreaseCacheSize(boolean automaticallyIncreaseCache) {
		automaticallyIncreaseCacheSize = automaticallyIncreaseCache;
	}

	public static int getIdBlockSize() {
		return idBlockSize;
	}

	public static void setIdBlockSize(int idBlockSize) {
		OdbConfiguration.idBlockSize = idBlockSize;
	}

	public static int getNB_IDS_PER_BLOCK() {
		return NB_IDS_PER_BLOCK;
	}

	public static int getID_BLOCK_REPETITION_SIZE() {
		return ID_BLOCK_REPETITION_SIZE;
	}

	public static boolean inPlaceUpdate() {
		return inPlaceUpdate;
	}

	/*
	 * The in place update feature is going to be removed public static void
	 * setInPlaceUpdate(boolean inPlaceUpdate) { OdbConfiguration.inPlaceUpdate
	 * = inPlaceUpdate; }
	 */

	/**
	 * @return Returns the stringSpaceReserveFactor.
	 */
	public static int getStringSpaceReserveFactor() {
		return stringSpaceReserveFactor;
	}

	/**
	 * @param stringSpaceReserveFactor
	 *            The stringSpaceReserveFactor to set.
	 */
	public static void setStringSpaceReserveFactor(int stringSpaceReserveFactor) {
		OdbConfiguration.stringSpaceReserveFactor = stringSpaceReserveFactor;
	}

	/**
	 * @return Returns the debugLevel.
	 */
	public static int getDebugLevel() {
		return debugLevel;
	}

	/**
	 * @param debugLevel
	 *            The debugLevel to set.
	 */
	public static void setDebugLevel(int debugLevel) {
		OdbConfiguration.debugLevel = debugLevel;
	}

	public static int getDefaultBufferSizeForTransaction() {
		return defaultBufferSizeForTransaction;
	}

	public static void setDefaultBufferSizeForTransaction(int defaultBufferSizeForTransaction) {
		OdbConfiguration.defaultBufferSizeForTransaction = defaultBufferSizeForTransaction;
	}

	public static int getNbBuffers() {
		return nbBuffers;
	}

	public static void setNbBuffers(int nbBuffers) {
		OdbConfiguration.nbBuffers = nbBuffers;
	}

	public static boolean useMultiBuffer() {
		return useMultiBuffer;
	}

	public static void setUseMultiBuffer(boolean useMultiBuffer) {
		OdbConfiguration.useMultiBuffer = useMultiBuffer;
	}

	public static boolean checkModelCompatibility() {
		return checkModelCompatibility;
	}

	public static void setCheckModelCompatibility(boolean checkModelCompatibility) {
		OdbConfiguration.checkModelCompatibility = checkModelCompatibility;
	}

	public static boolean automaticCloseFileOnExit() {
		return automaticCloseFileOnExit;
	}

	public static void setAutomaticCloseFileOnExit(boolean automaticFileClose) {
		OdbConfiguration.automaticCloseFileOnExit = automaticFileClose;
	}

	public static boolean isLogAll() {
		return logAll;
	}

	public static void setLogAll(boolean logAll) {
		OdbConfiguration.logAll = logAll;
	}

	public static boolean logServerConnections() {
		return logServerConnections;
	}

	public static void setLogServerConnections(boolean logServerConnections) {
		OdbConfiguration.logServerConnections = logServerConnections;
	}

	public static int getDefaultIndexBTreeDegree() {
		return defaultIndexBTreeDegree;
	}

	public static void setDefaultIndexBTreeDegree(int defaultIndexBTreeSize) {
		OdbConfiguration.defaultIndexBTreeDegree = defaultIndexBTreeSize;
	}

	public static boolean useLazyCache() {
		return useLazyCache;
	}

	public static void setUseLazyCache(boolean useLazyCache) {
		OdbConfiguration.useLazyCache = useLazyCache;
	}

	/**
	 * @return the queryExecutorCallback
	 */
	public static IQueryExecutorCallback getQueryExecutorCallback() {
		return queryExecutorCallback;
	}

	/**
	 * @param queryExecutorCallback
	 *            the queryExecutorCallback to set
	 */
	public static void setQueryExecutorCallback(IQueryExecutorCallback queryExecutorCallback) {
		OdbConfiguration.queryExecutorCallback = queryExecutorCallback;
	}

	/**
	 * @return the useCache
	 */
	public static boolean useCache() {
		return useCache;
	}

	/**
	 * @param useCache
	 *            the useCache to set
	 */
	public static void setUseCache(boolean useCache) {
		OdbConfiguration.useCache = useCache;
	}

	public static boolean isMonitoringMemory() {
		return monitorMemory;
	}

	public static void monitorMemory(boolean yes) {
		monitorMemory = yes;
	}

	public static boolean displayWarnings() {
		return displayWarnings;
	}

	public static void setDisplayWarnings(boolean yesOrNo) {
		displayWarnings = yesOrNo;
	}

	public static boolean saveHistory() {
		return saveHistory;
	}

	public static void setSaveHistory(boolean saveTheHistory) {
		saveHistory = saveTheHistory;
	}

	public static int getScaleForAverageDivision() {
		return scaleForAverageDivision;
	}

	public static void setScaleForAverageDivision(int scaleForAverageDivision) {
		OdbConfiguration.scaleForAverageDivision = scaleForAverageDivision;
	}

	public static int getRoundTypeForAverageDivision() {
		return roundTypeForAverageDivision;
	}

	public static void setRoundTypeForAverageDivision(int roundTypeForAverageDivision) {
		OdbConfiguration.roundTypeForAverageDivision = roundTypeForAverageDivision;
	}

	public static boolean enableEmptyConstructorCreation() {
		return enableEmptyConstructorCreation;
	}

	public static void setEnableEmptyConstructorCreation(boolean enableEmptyConstructorCreation) {
		OdbConfiguration.enableEmptyConstructorCreation = enableEmptyConstructorCreation;
	}

	public static Class getIOClass() {
		return ioClass;
	}

	public static void setIOClass(Class IOClass, String password) {
		ioClass = IOClass;
		encryptionPassword = password;
	}

	public static String getEncryptionPassword() {
		return encryptionPassword;
	}

	public static ICoreProvider getCoreProvider() {
		if (!coreProviderInit) {
			coreProviderInit = true;
			try {
				coreProvider.init2();
			} catch (Exception e) {
				throw new ODBRuntimeException(NeoDatisError.ERROR_IN_CORE_PROVIDER_INITIALIZATION.addParameter("Core Provider"), e);
			}

		}
		return coreProvider;
	}

	public static void setCoreProvider(ICoreProvider coreProvider) {
		OdbConfiguration.coreProvider = coreProvider;
	}

	public static String getDatabaseCharacterEncoding() {
		return databaseCharacterEncoding;
	}

	public static void setDatabaseCharacterEncoding(String dbCharacterEncoding) throws UnsupportedEncodingException {
		if (dbCharacterEncoding != null) {
			// Checks if encoding is valid, using it in the String.getBytes
			// method
			new DefaultByteArrayConverter().testEncoding(dbCharacterEncoding);
			OdbConfiguration.databaseCharacterEncoding = dbCharacterEncoding;
		} else {
			//neodatisee
			throw new ODBRuntimeException(NeoDatisError.NULL_ENCODING);
		}
	}

	public static void setLatinDatabaseCharacterEncoding() throws UnsupportedEncodingException {
		OdbConfiguration.databaseCharacterEncoding = defaultDatabaseCharacterEncoding;
	}

	public static boolean hasEncoding() {
		return databaseCharacterEncoding != null;
	}

	public static ClassLoader getClassLoader() {
		return classLoader;
	}

	public static void setClassLoader(ClassLoader classLoader) {
		OdbConfiguration.classLoader = classLoader;
		OdbConfiguration.getCoreProvider().getClassIntrospector().reset();
		OdbConfiguration.getCoreProvider().getClassPool().reset();

	}

	public static boolean checkRuntimeVersion() {
		return checkRuntimeVersion;
	}

	public static void setCheckRuntimeVersion(boolean checkJavaRuntimeVersion) {
		OdbConfiguration.checkRuntimeVersion = checkJavaRuntimeVersion;
	}

	/**
	 * @return
	 */
	public static Class getMessageStreamerClass() {
		return messageStreamerClass;
	}

	public static void setMessageStreamerClass(Class messageStreamerClass) {
		OdbConfiguration.messageStreamerClass = messageStreamerClass;
	}

	public static boolean logServerStartupAndShutdown() {
		return logServerStartupAndShutdown;
	}

	public static void setLogServerStartupAndShutdown(boolean logServerStartup) {
		OdbConfiguration.logServerStartupAndShutdown = logServerStartup;
	}

	public static boolean useIndex() {
		return useIndex;
	}

	public static void setUseIndex(boolean useIndex) {
		OdbConfiguration.useIndex = useIndex;
	}

	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	public static void setDebugEnabled(boolean debugEnabled) {
		OdbConfiguration.debugEnabled = debugEnabled;
	}

	public static boolean shareSameVmConnectionMultiThread() {
		return shareSameVmConnectionMultiThread;
	}

	public static void setShareSameVmConnectionMultiThread(boolean shareSameVmConnectionMultiThread) {
		OdbConfiguration.shareSameVmConnectionMultiThread = shareSameVmConnectionMultiThread;
	}

	/**
	 * 
	 */
	public static void lockObjectsOnSelect(boolean yesNo) {
		lockObjectsOnSelect = yesNo;
	}
	public static boolean lockObjectsOnSelect() {
		return lockObjectsOnSelect;
	}

	public static long getTimeoutToAcquireMutexInMultiThread() {
		return timeoutToAcquireMutexInMultiThread;
	}

	public static void setTimeoutToAcquireMutexInMultiThread(long timeoutToAcquireMutexInMultiThread) {
		OdbConfiguration.timeoutToAcquireMutexInMultiThread = timeoutToAcquireMutexInMultiThread;
	}

	/**
	 * @return
	 */
	public static boolean multiThreadExclusive() {
		return multiThreadExclusive;
	}

	public static void setMultiThreadExclusive(boolean trueOrFalse){
		multiThreadExclusive = trueOrFalse;
	}
	
	public static void registerDatabaseStartupManager(DatabaseStartupManager manager){
		databaseStartupManager = manager;
	}
	public static void removeDatabaseStartupManager(){
		databaseStartupManager = null;
	}
	public static DatabaseStartupManager getDatabaseStartupManager(){
		return databaseStartupManager;
	}
	
}
