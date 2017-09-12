package com.createlier.freetime.services.shared;


import com.createlier.freetime.exceptions.SharedServicesException;
import com.createlier.freetime.services.ServiceConnector;
import com.createlier.freetime.services.ServiceRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Shared Services Thread
 *
 * @author Pedro Henrique
 * 
 * 
 */
final public class SharedThreadGroup extends ThreadGroup {
	
	/**
	 * Task
	 * 
	 * @author user
	 *
	 */
	final private class Service {
		public Future<Void> future;
		public Object identifier;
		public int identifierMode;
		public ServiceRunnable runnable;
		public ServiceConnector connector;
	}
	
	// Final Private Variables
	final private SharedServicesManager mServicesManager;
	final private List<Service> mServices = new ArrayList<Service>();
	final private ExecutorService mExecutorServices;
	
	// Private Variables
	volatile private int mServiceIdentifierRotule = 0;
	
	/**
	 * Constructor
	 *
	 */
	public SharedThreadGroup(final SharedServicesManager servicesManager, final SharedServicesManager.Mode mode) {
		super(Thread.currentThread().getThreadGroup(), "SharedServicesThreadGroup");
		
		mServicesManager = servicesManager;
		
		// Thread Factory
		final ThreadFactory threadFactory = new ThreadFactory() {
			
			/**
			 * On New Thread
			 */
			@Override
			public Thread newThread(Runnable r) {
				final Thread thread = new Thread(SharedThreadGroup.this, r);
				thread.setDaemon(true);
				return thread;
			}
		};
		
		// Executor
		mExecutorServices = new ThreadPoolExecutor(mode.getMaxThreads(), mode.getMaxThreads(),
				0L, TimeUnit.MILLISECONDS,
				mode.instantiate(10),
				threadFactory) {

			/**
			 * After Execute
			 *
			 * @param r
			 * @param t
			 */
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				super.afterExecute(r, t);
				if (t == null) {
					try {
						((Future) r).get();
					} catch (CancellationException ce) {
						t = ce;
					} catch (ExecutionException ee) {
						t = ee.getCause();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
					}
				}
				if (t != null) {
					RuntimeException e = new RuntimeException();
					e.setStackTrace(t.getStackTrace());
					throw e;
				}
			}
		};
	}
	
	/**
	 * Return True if has Service Thread from Runnable
	 *
	 * @return
	 */
	final protected boolean hasServiceThread(final int identifier) {
		synchronized (mServices) {
			for(final Service service : mServices) {
				if(service.identifierMode == 0 && (Integer)service.identifier == identifier) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return True if has Service Thread from Runnable
	 *
	 * @return
	 */
	final protected boolean hasServiceThread(final String identifier) {
		synchronized (mServices) {
			for(final Service service : mServices) {
				if(service.identifierMode == 1 && service.identifier.equals(identifier)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Add Service Thread
	 */
	final protected int addServiceThread(final ServiceRunnable runnable) {
		int serviceIdentifier = 0;
		synchronized (mServices) {
			serviceIdentifier = mServiceIdentifierRotule++;
			final ServiceConnector connector = new ServiceConnector();
			final SharedService serviceRunnable = new SharedService(mServicesManager, serviceIdentifier, runnable, connector);
			final Future<Void> future = mExecutorServices.submit(serviceRunnable);
			final Service service = new Service();
			service.identifier = serviceIdentifier;
			service.identifierMode = 0;
			service.future = future;
			service.runnable = runnable;
			service.connector = connector;
			mServices.add(service);
		}
		return serviceIdentifier;
	}
	
	/**
	 * Add Service Thread
	 */
	final protected void addServiceThread(final ServiceRunnable runnable, final String identifier) {
		if(hasServiceThread(identifier))
			throw new SharedServicesException("There is already a service running with the same identifier.");
		synchronized (mServices) {
			final ServiceConnector connector = new ServiceConnector();
			final SharedService serviceRunnable = new SharedService(mServicesManager, identifier, runnable, connector);
			final Future<Void> future = mExecutorServices.submit(serviceRunnable);
			final Service service = new Service();
			service.identifier = identifier;
			service.identifierMode = 1;
			service.future = future;
			service.runnable = runnable;
			service.connector = connector;
			mServices.add(service);
		}
	}
	
	/**
	 * Get Service Runnable
	 */
	final protected ServiceRunnable getService(final int identifier) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service service = itr.next();
				if(service.identifierMode == 0 && (Integer)service.identifier == identifier)
					return service.runnable;
			}
		}
		return null;
	}
	
	/**
	 * Remove Service Thread
	 */
	final protected ServiceRunnable getService(final String identifier) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service service = itr.next();
				if(service.identifierMode == 1 && service.identifier.equals(identifier))
					return service.runnable;
			}
		}
		return null;
	}

	/**
	 * Remove Service Connector
	 */
	final protected ServiceConnector getServiceConnector(final String identifier) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service service = itr.next();
				if(service.identifierMode == 1 && service.identifier.equals(identifier))
					return service.connector;
			}
		}
		return null;
	}

	/**
	 * Get Service Connector
	 */
	final protected ServiceConnector getServiceConnector(final int identifier) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service service = itr.next();
				if(service.identifierMode == 0 && (Integer)service.identifier == identifier)
					return service.connector;
			}
		}
		return null;
	}

	/**
	 * Remove Service Thread
	 */
	final protected void removeServiceThread(final int identifier) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service service = itr.next();
				if(service.identifierMode == 0 && (Integer)service.identifier == identifier) {
					// Close Thread
					service.future.cancel(true);
					// Remove from Stack
					itr.remove();
				}
			}
		}
	}
	
	/**
	 * Remove Service Thread
	 */
	final protected void removeServiceThread(final String identifier) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service service = itr.next();
				if(service.identifierMode == 1 && service.identifier.equals(identifier)) {
					// Close Thread
					service.future.cancel(true);
					// Remove from Stack
					itr.remove();
				}
			}
		}
	}
	
	/**
	 * Remove Service Control
	 * 
	 * @param service
	 */
	final protected void removeServiceControl(final SharedService service) {
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service sService = itr.next();
				if(service.getIdentifier().equals(sService.identifier)) {
					// Remove from Stack
					itr.remove();
					// Service Removed
					return;
				}
			}
		}
	}
	
	/**
	 * Wait for Service End
	 */
	final protected void waitForService(final int identifier) {
		Service service = null;
		// Lock
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service findService = itr.next();
				if(findService.identifierMode == 0 && (Integer)findService.identifier == identifier) {
					service = findService;
					break;
				}
			}
		}
		//
		if(service == null)
			return;
		try {
			service.future.get();
		} catch (Exception e) {
			throw new SharedServicesException(e.getMessage());
		}
	}
	
	/**
	 * Wait for Service End
	 */
	final protected void waitForService(final String identifier) {
		Service service = null;
		// Lock
		synchronized (mServices) {
			Iterator<Service> itr = mServices.iterator();
			while(itr.hasNext()) {
				final Service findService = itr.next();
				if(findService.identifierMode == 1 && findService.identifier.equals(identifier)) {
					service = findService;
					break;
				}
			}
		}
		//
		if(service == null)
			return;
		try {
			service.future.get();
		} catch (Exception e) {
			throw new SharedServicesException(e.getMessage());
		}
	}
	
	/**
	 * Close Group
	 */
	final protected void close() {
		this.interrupt();
		synchronized (mServices) {
			for(final Service service : mServices)
				service.future.cancel(true);
		}
		mExecutorServices.shutdownNow();
		/**
		try {
			mExecutorServices.awaitTermination(10, TimeUnit.SECONDS);
		} catch(Exception e) {}
		if(mExecutorServices.isTerminated())
			destroy();*/
	}
}
