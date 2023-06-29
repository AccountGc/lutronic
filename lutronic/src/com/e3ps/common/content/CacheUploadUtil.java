package com.e3ps.common.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.time.Duration;
import java.time.Instant;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.VersionReferenceQueryStringDelegate;
import wt.fv.FileFolder;
import wt.fv.FvHelper;
import wt.fv.FvMount;
import wt.fv.FvProperties;
import wt.fv.FvTransaction;
import wt.fv.FvVault;
import wt.fv.StandardFvService;
import wt.fv.StoreStreamListener;
import wt.fv.StoredItem;
import wt.fv.Vault;
import wt.fv.configurator.FolderDesc;
import wt.fv.configurator.RootFolderDesc;
import wt.fv.master.ReplicaVault;
import wt.fv.replica.ReplicaServerHelper;
import wt.fv.replica.StandardReplicaService;
import wt.fv.uploadtocache.BackupedFile;
import wt.fv.uploadtocache.CacheDescriptor;
import wt.fv.uploadtocache.CachedContentDescriptor;
import wt.fv.uploadtocache.UploadToCacheHelper;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.objectstorage.ContentFileWriter;
import wt.objectstorage.ContentManagerFactory;
import wt.objectstorage.ContentStorageManager;
import wt.objectstorage.exception.ContentFileAlreadyExistsException;
import wt.objectstorage.exception.ContentFileCanNotBeStoredException;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

/**
 * @author hwang
 */
public class CacheUploadUtil implements RemoteAccess {
    private static final Logger LOGGER = Logger.getLogger(CacheUploadUtil.class);

    private static final String RESOURCE = "wt.fv.uploadtocache.uploadtocacheResource";

    public static final String UPLOAD_FEEDBACK = "uploadFeedback";
    private static final String CLIENT_LOCALE = "cLocale";

    private enum MultiPartUploadMode {
	MULTIFILE, MULTICHAPTER
    };

    /**
     * <pre>
     * &#64;desc  :
     * &#64;author : hwang
     * &#64;date   : 2015. 7. 2.오전 10:18:47
     * </pre>
     * 
     * @method : getCacheDescriptor
     * @param fileNum
     * @param paramBoolean
     * @param userId
     * @param masterHost
     * @return
     * @throws WTException
     */

    public static CacheDescriptor getCacheDescriptor(int fileNum, boolean paramBoolean, String userId,
	    String masterHost) throws WTException {
	Object obj = null;
	try {
	    URL url = new URL(masterHost);

	    RemoteMethodServer server = RemoteMethodServer.getInstance(url);

	    // server.setAuthenticator(AuthHandler.getMethodAuthenticator(url,
	    // userId));

	    Class argTypes[] = new Class[] { int.class, boolean.class, String.class };
	    Object args[] = new Object[] { fileNum, paramBoolean, userId };

	    obj = server.invoke("_getCacheDescriptor", CacheUploadUtil.class.getName(), null, argTypes, args);
	} catch (InvocationTargetException | RemoteException | MalformedURLException e) {
	    throw new WTException(e);
	}

	return (CacheDescriptor) obj;
    }

    /**
     * <pre>
     * &#64;desc  :
     * &#64;author : hwang
     * &#64;date   : 2015. 7. 2.오전 10:19:01
     * </pre>
     * 
     * @method : getLocalVault
     * @param paramLong
     * @return
     * @throws WTException
     */
    public static Vault getLocalVault(long paramLong) throws WTException {

	Class localReplicaVault = (FvProperties.FORCE_ONE_VAULT) ? FvVault.class : ReplicaVault.class;
	QuerySpec localQuerySpec = new QuerySpec(localReplicaVault);

	localQuerySpec.appendWhere(
		new SearchCondition(Vault.class, "thePersistInfo.theObjectIdentifier.id", "=", paramLong), new int[0]);

	QueryResult localQueryResult = PersistenceServerHelper.manager.query(localQuerySpec);

	if (!(localQueryResult.hasMoreElements())) {
	    return null;
	}
	Vault localVault = (Vault) localQueryResult.nextElement();
	return localVault;

    }

    /**
     * <pre>
     * &#64;desc  :
     * &#64;author : hwang
     * &#64;date   : 2015. 7. 2.오전 10:18:34
     * </pre>
     * 
     * @method : _getCacheDescriptor
     * @param fileNum
     * @param paramBoolean
     * @param userId
     * @return
     * @throws Exception
     */
    public static CacheDescriptor _getCacheDescriptor(int fileNum, boolean paramBoolean, String userId)
	    throws WTException {
	// System.out.println("call _getCacheDescriptor ");

	boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
	SessionContext sessioncontext = SessionContext.newContext();

	try {

	    SessionHelper.manager.setAuthenticatedPrincipal(userId);
	    return _getCacheDescriptor(fileNum, paramBoolean);
	    // } catch (WTException e) {
	    // e.printStackTrace();
	} finally {
	    SessionContext.setContext(sessioncontext);
	    SessionServerHelper.manager.setAccessEnforced(bool);
	}

	// return null;

    }

    /**
     * <pre>
     * &#64;desc  :
     * &#64;author : hwang
     * &#64;date   : 2015. 7. 2.오전 10:20:58
     * </pre>
     * 
     * @method : _getCacheDescriptor
     * @param fileNum
     * @param paramBoolean
     * @return
     * @throws WTException
     */
    public static CacheDescriptor _getCacheDescriptor(int fileNum, boolean paramBoolean) throws WTException {
	CacheDescriptor localCacheDescriptor = UploadToCacheHelper.service.getCacheDescriptor(fileNum, paramBoolean);
	UploadToCacheHelper.service.getCacheDescriptor(fileNum, paramBoolean);
	return localCacheDescriptor;
    }

    /**
     * <pre>
     * &#64;desc  :
     * &#64;author : hwang
     * &#64;date   : 2015. 7. 2.오전 10:19:06
     * </pre>
     * 
     * @method : doUploadToCache
     * @param localCacheDs
     * @param file
     * @param isMainServer
     * @return
     * @throws WTException
     */
    public static CachedContentDescriptor doUploadToCache(CacheDescriptor localCacheDs, File file, boolean isMainServer)
	    throws WTException {
	// if (!wt.method.RemoteMethodServer.ServerFlag) {
	// Class c[] = new Class[] { CacheDescriptor.class, File.class , boolean.class};
	// Object o[] = new Object[] { localCacheDs, file, isMainServer };
	// return (CachedContentDescriptor) RemoteMethodServer.getDefault()
	// .invoke("doUploadToCache", CacheUploadUtil.class.getName(),
	// null, c, o);
	// }
	CachedContentDescriptor descriptor = null;
	try {
	    long folderId = localCacheDs.getFolderId();
	    long fileName = localCacheDs.getFileNames()[0];
	    long vaultId = localCacheDs.getVaultId();
	    long streamId = localCacheDs.getStreamIds()[0];

	    String masterUrl = localCacheDs.getMasterUrl();

	    // String url = UploadToCacheHelper.service.getUploadToCacheURL();
	    // System.out.println("url == " + url);

	    // URL uu = new URL(url);
	    // uu.getHost();

	    // System.out.println(localCacheDs);

	    InputStream[] streams = new InputStream[1];
	    streams[0] = new FileInputStream(file);
	    //System.out.println("streams[0] =" + streams[0]);
	    long[] fileSize = new long[1];
	    fileSize[0] = file.length();

	    String[] paths = new String[1];
	    paths[0] = file.getPath();

	    if (isMainServer) {
		Vault vault = CacheUploadUtil.getLocalVault(vaultId);
		storeContentInVaultOnMaster(vault, folderId, fileName, 0, streams[0], fileSize);
	    } else {
		storeContentInVaultOnRemote(masterUrl, vaultId, folderId, fileName, 0, streams[0], fileSize);
	    }

	    descriptor = new CachedContentDescriptor(streamId, folderId, fileSize[0], 0, file.getPath());
	} catch (FileNotFoundException e) {
	    throw new WTException(e);
	}
	return descriptor;
    }

    /*
     * private static long storeContentInVaultOnMaster_OLD(Vault paramVault, long folderId, long contentFileName,
     * long paramLong3, InputStream paramInputStream, long[] contentSizes) throws WTException {
     * boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
     * FileFolder localFileFolder = null;
     * StoreStreamListener localStoreStreamListener = new StoreStreamListener();
     * FvTransaction localFvTransaction = new FvTransaction();
     * try {
     * localFvTransaction.start();
     * localFvTransaction.addTransactionListener(localStoreStreamListener);
     * if (paramVault == null) throw new WTException("wt.fv.uploadtocache.uploadtocacheResource", "10", null);
     * localStoreStreamListener.prepareToGetFolder(paramVault);
     * SessionContext localSessionContext = SessionContext.newContext();
     * try {
     * SessionHelper.manager.setAdministrator();
     * localFileFolder = StandardFvService.getActiveFolder(paramVault);
     * } finally {
     * SessionContext.setContext(localSessionContext);
     * }
     * if (localFileFolder == null) throw new WTException("wt.fv.uploadtocache.uploadtocacheResource", "10", null);
     * localStoreStreamListener.informGotFolderOk();
     * FvMount localFvMount = StandardFvService.getLocalMount(localFileFolder);
     * if (localFvMount == null) {
     * throw new WTException("wt.fv.uploadtocache.uploadtocacheResource", "11", null);
     * }
     * String str1 = localFvMount.getPath();
     * String str2 = StoredItem.buildFileName(contentFileName);
     * String str0 = "";
     * BackupedFile localBackupedFile = new BackupedFile(str0, str1, str2);
     * localStoreStreamListener.prepareToUpload(localFileFolder, localBackupedFile, str1, str2);
     * FvFileStreamWriter localFvFileStreamWriter = new FvFileStreamWriter();
     * long l1 = -1L;
     * // try {
     * MethodContext.releaseInactiveConnectionStatic();
     * // } catch (Exception localException) {
     * // if (localException instanceof WTException) {
     * // throw ((WTException) localException);
     * // }
     * // throw new WTException(localException);
     * // }
     * try {
     * l1 = localFvFileStreamWriter.storeStream(paramInputStream, localBackupedFile, str2, contentSizes[0],
     * false);
     * if (contentSizes[0] <= 0L) contentSizes[0] = l1;
     * } catch (FvFileAlreadyExists localFvFileAlreadyExists) {
     * localStoreStreamListener.setContentFile(null);
     * throw localFvFileAlreadyExists;
     * } catch (FvFileCanNotBeStored localFvFileCanNotBeStored) {
     * if (localFvFileStreamWriter.isWriteException()) {
     * localStoreStreamListener.informWriteFailed();
     * }
     * throw localFvFileCanNotBeStored;
     * }
     * localFvTransaction.commit();
     * localFvTransaction = null;
     * long l2 = PersistenceHelper.getObjectIdentifier(localFileFolder).getId();
     * return l2;
     * } finally {
     * if (localFvTransaction != null) {
     * localFvTransaction.rollback();
     * }
     * SessionServerHelper.manager.setAccessEnforced(bool);
     * }
     * }
     */
    private static void PP(String arg) {
	//System.out.println("DoUploadToCache_Server: " + arg);
    }

    /**
     * New 11.0 버전
     * 
     * @param vault
     * @param folderId
     * @param contentFileName
     * @param contentStreamId
     * @param contentStream
     * @param contentSize
     * @return
     * @throws WTException
     * @메소드명 :
     * @작성자 : TaeSik, Eom
     * @작성일 : 2018. 01. 12
     * @설명 :
     * @수정이력 - 수정일,수정자,수정내용
     */
    private static long storeContentInVaultOnMaster(Vault vault, long folderId, long contentFileName,
	    long contentStreamId, InputStream contentStream, long[] contentSize) throws WTException {
	long arg22 = 1;
	boolean current = SessionServerHelper.manager.setAccessEnforced(false);
	FileFolder folder = null;
	StoreStreamListener listener = new StoreStreamListener();
	FvTransaction trx = new FvTransaction();

	try {
	    PP("storeContentInLocalMasterVault parameters");
	    PP("          vaultId = " + vault.getIdentity());
	    PP("         folderId = " + folderId);
	    PP("  contentFileName = " + contentFileName);
	    PP("      contentSize = " + contentSize[0]);

	    trx.start();
	    trx.addTransactionListener(listener);
	    if (vault == null) throw new WTException(RESOURCE,
		    wt.fv.uploadtocache.uploadtocacheResource.CACHE_VAULT_OR_FOLDER_DOESNT_EXIST_ON_LOCAL_MASTER, null);
	    listener.prepareToGetFolder(vault);
	    SessionContext previous = SessionContext.newContext();
	    try {
		SessionHelper.manager.setAdministrator();
		folder = StandardFvService.getActiveFolder(vault);
	    } finally {
		SessionContext.setContext(previous);
	    }

	    if (folder == null) throw new WTException(RESOURCE,
		    wt.fv.uploadtocache.uploadtocacheResource.CACHE_VAULT_OR_FOLDER_DOESNT_EXIST_ON_LOCAL_MASTER, null);
	    listener.informGotFolderOk();
	    FvMount mount = StandardFvService.getLocalMount(folder);

	    if (mount == null)
		throw new WTException(RESOURCE, wt.fv.uploadtocache.uploadtocacheResource.CANT_SAVE_NO_LOC_MOUNT, null);

	    String path = mount.getPath();
	    PP("                              path = <" + path + ">");

	    String fn = StoredItem.buildFileName(contentFileName);
	    PP("                              fn   = <" + fn + ">");
	    String mountType = FvHelper.service.getMountType(mount);
	    BackupedFile contentFile = new BackupedFile(mountType, path, fn);

	    try {
		listener.prepareToUpload(folder, contentFile, path, fn);
	    } catch (ContentFileCanNotBeStoredException arg37) {
		PP("Error in prepareToUpload: informing listener that write has failed for folder - " + folder);
		listener.informWriteFailed();
		throw arg37;
	    }
	    ContentStorageManager arg18 = ContentManagerFactory.getContentManager(mountType);
	    ContentFileWriter arg19 = arg18.getContentFileWriter(contentFile.getFirstContentFile());
	    long arg20 = -1L;

	    try {
		MethodContext.releaseInactiveConnectionStatic();
	    } catch (Exception arg39) {
		if (arg39 instanceof WTException) {
		    throw (WTException) arg39;
		}

		throw new WTException(arg39);
	    }

	    try {
		arg20 = arg19.storeStream(contentStream, contentFile, contentSize[0], false);

		PP("ContentFileWriter. storeStream =" + arg20);
		if (contentSize[0] <= 0L) {
		    contentSize[0] = arg20;
		}
	    } catch (ContentFileAlreadyExistsException arg40) {
		listener.setContentFile((BackupedFile) null);
		throw arg40;
	    } catch (ContentFileCanNotBeStoredException arg41) {
		if (arg19.isWriteException()) {
		    PP("informing listener to mark folder read only - " + folder);
		    listener.informWriteFailed();
		}

		throw arg41;
	    }
	    trx.commit();
	    trx = null;
	    arg22 = PersistenceHelper.getObjectIdentifier(folder).getId();
	} finally {
	    if (trx != null) {
		trx.rollback();
	    }
	    SessionServerHelper.manager.setAccessEnforced(current);
	}

	return arg22;

    }

    /*
     * private static long storeContentInVaultOnRemote_OLD(String masterURL, long vaultId, long folderId, long fileName,
     * long paramLong4, InputStream paramInputStream, long[] sizes) throws WTException {
     * FolderDesc localFolderDesc = null;
     * Vector<FolderDesc> localVector = new Vector<FolderDesc>(); // no PMD // NOPMD by hwang on 15. 7. 2 오전 10:18
     * try {
     * localFolderDesc = ReplicaServerHelper.service.findFolderToSaveFile(masterURL, vaultId, localVector);
     * String str1 = localFolderDesc.getPath();
     * String str2 = StoredItem.buildFileName(fileName);
     * BackupedFile localBackupedFile = new BackupedFile(str1, str2);
     * FvFileStreamWriter localFvFileStreamWriter = new FvFileStreamWriter();
     * long l = -1L;
     * try {
     * l = localFvFileStreamWriter.storeStream(paramInputStream, localBackupedFile, str2, sizes[0], false);
     * if (sizes[0] <= 0L) sizes[0] = l;
     * } catch (FvFileCanNotBeStored localFvFileCanNotBeStored) {
     * if (localFvFileStreamWriter.isWriteException()) {
     * }
     * throw localFvFileCanNotBeStored;
     * }
     * } finally {
     * StandardReplicaService.markFoldersReadOnlyOnMaster(localVector, masterURL);
     * }
     * return localFolderDesc.getId();
     * }
     */
    private static long storeContentInVaultOnRemote(String masterURL, long vaultOnRemoteSiteId, long folderId,
	    long contentFileName, long contentStreamId, InputStream contentStream, long[] contentSize)
	    throws WTException {

	PP("storeContentInVaultOnRemote parameters");
	PP("            masterURL = " + masterURL);
	PP("  vaultOnRemoteSiteId = " + vaultOnRemoteSiteId);
	PP("             folderId = " + folderId);
	PP("      contentFileName = " + contentFileName);
	PP("          contentSize = " + contentSize[0]);
	if (false) { // debug version - print info from stream only!
	    int intContentSize = (int) contentSize[0];
	    byte[] bb = new byte[intContentSize];
	    int l = 0;
	    while (true)
		try {
		    int bs = (intContentSize - l > 1024 ? 1024 : intContentSize - l);
		    int n = contentStream.read(bb, l, bs);
		    if (n <= 0) break;
		    l += n;
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    PP("  read from stream: expect = c " + intContentSize + ", really = " + l);
	    String s = new String(bb);
	    PP("  read value: <" + s + ">");
	}

	FolderDesc folderDesc = null;
	Vector<FolderDesc> foldersToMarkRO = new Vector<FolderDesc>();
	try {

	    folderDesc = ReplicaServerHelper.service.findFolderToSaveFile(masterURL, vaultOnRemoteSiteId,
		    foldersToMarkRO);
	    PP("storeContentInVaultOnRemote 01");
	    String path = folderDesc.getPath();

	    PP(" path =<" + path + ">");
	    String fn = StoredItem.buildFileName(contentFileName);
	    PP(" fn =<" + fn + ">");
	    RootFolderDesc rootFolder = folderDesc.getRootFolderDesc();
	    String mountType = null;
	    if (rootFolder != null) {
		mountType = rootFolder.getMountType();
	    }
	    PP("Accepting content to be written to directory=" + path + ", file name=" + fn);
	    BackupedFile contentFile = new BackupedFile(mountType, path, fn);
	    ContentStorageManager arg17 = ContentManagerFactory.getContentManager(mountType);
	    ContentFileWriter arg18 = arg17.getContentFileWriter(contentFile.getFirstContentFile());
	    long arg19 = -1L;

	    PP("Successfully wrote " + contentSize[0] + " to file " + contentFile.getAbsolutePath());

	    try {
		Instant arg21 = Instant.now();
		arg19 = arg18.storeStream(contentStream, contentFile, contentSize[0], false);
		Instant arg22 = Instant.now();
		double arg23 = (double) Duration.between(arg21, arg22).toMillis() / 1000.0D;
		double arg25 = (double) arg19 / arg23;
		PP("DoUploadToCache_Server.storeContentInVaultOnRemoteObjectStorage: " + arg19
			+ " bytes successfully uploaded.");
		PP("DoUploadToCache_Server.storeContentInVaultOnRemoteObjectStorage: upload speed: "
			+ FvHelper.formatDownloadSpeed(arg25));
		if (contentSize[0] <= 0L) {
		    contentSize[0] = arg19;
		}
	    } catch (ContentFileCanNotBeStoredException arg30) {
		if (arg18.isWriteException()) {
		    FvProperties.getInfoLogger()
			    .info("storeContentInVaultOnRemote() ::- marking folder readonly - " + folderDesc);
		    folderDesc.setReadOnly(true);
		    FvProperties.getInfoLogger()
			    .info("storeContentInVaultOnRemote() ::- marking root folder readonly - "
				    + folderDesc.getRootFolderDesc());
		    folderDesc.getRootFolderDesc().setReadOnly(true);
		    foldersToMarkRO.add(folderDesc);
		}

		throw arg30;
	    }

	    StandardReplicaService.updateNoOfFilesCount(masterURL, folderDesc.getId(), 1);
	    PP("Successfully wrote " + contentSize[0] + " to file " + contentFile.getAbsolutePath());
	} finally {
	    StandardReplicaService.markFoldersReadOnlyOnMaster(foldersToMarkRO, masterURL);
	}
	return folderDesc.getId();
    }

    /**
     * @desc
     * @author khkim
     * @date 2015. 7. 31.
     * @param id
     * @param masterHost
     * @return
     * @throws Exception
     */
    public static String getUserId(long id, String masterHost) throws Exception {
	int index = masterHost.indexOf("/servlet/WindchillGW");
	masterHost = masterHost.substring(0, index + 1);

	URL url = new URL(masterHost);

	RemoteMethodServer server = RemoteMethodServer.getInstance(url);

	// System.out.println("server ========= " + server);
	LOGGER.debug("server ========= " + server);

	Class[] argTypes = { Long.TYPE };
	Object[] args = { Long.valueOf(id) };

	Object obj = server.invoke("_getUserId", CacheUploadUtil.class.getName(), null, argTypes, args);

	return (String) obj;
    }

    /**
     * @desc
     * @author khkim
     * @date 2015. 7. 31.
     * @param id
     * @return
     * @throws Exception
     */
    public static String _getUserId(long id) throws Exception {
	LOGGER.debug("call _getCacheDescriptor");
	// System.out.println("call _getCacheDescriptor");

	boolean bool = SessionServerHelper.manager.setAccessEnforced(false);
	SessionContext sessioncontext = SessionContext.newContext();
	try {
	    SessionHelper.manager.setAdministrator();
	    String oid = WTUser.class.getName() + ":" + id;
	    WTUser user = (WTUser) getObject(oid);
	    return user.getName();
	} catch (WTException e) {
	    LOGGER.error(e.getLocalizedMessage());
	} finally {
	    SessionContext.setContext(sessioncontext);
	    SessionServerHelper.manager.setAccessEnforced(bool);
	}
	return null;
    }

    private static Persistable getObject(String oid) {
	if (oid == null) {
	    return null;
	}
	try {

	    ReferenceFactory rf = null;
	    if (oid.toUpperCase().startsWith("VR")) {
		VersionReferenceQueryStringDelegate delegate = new VersionReferenceQueryStringDelegate();
		Persistable obj = delegate.getReference(oid).getObject();
		return obj;
	    } else {
		rf = new ReferenceFactory();
	    }
	    return rf.getReference(oid).getObject();
	} catch (WTException e) {
	    LOGGER.error(e.getLocalizedMessage());
	}
	return null;
    }

    /**
     * <pre>
     * &#64;desc  :
     * &#64;author : hwang
     * &#64;date   : 2015. 7. 2.오전 10:21:03
     * </pre>
     * 
     * @method : main
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {
	// String masterUrl = "http://dsplmtest.dsec.co.kr/Windchill/servlet/WindchillGW";
	//
	// int index1 = masterUrl.indexOf("/servlet/WindchillGW");
	//
	// System.out.println(masterUrl.substring(index1 + 1));
	//
	// CacheDescriptor ds = getCacheDescriptor(1, true, "wcadmin",
	// "http://dsdev.dsec.co.kr/Windchill/");
	// System.out.println(ds);
	//
	// String uploadPath = "d:\\444" + File.separator + "5.xxx";
	// int index = uploadPath.lastIndexOf(File.separator);
	// System.out.println(File.separator + " " +
	// uploadPath.substring(index + 1));
    }
}
