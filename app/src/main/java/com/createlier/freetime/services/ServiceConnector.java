package com.createlier.freetime.services;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Pedro on 22/06/2016.
 */
public class ServiceConnector {

    /**
     * Message
     */
    final public class Message {

        //
        public int code;
        public Object object;

        /**
         * Constructor
         *
         * @param code
         * @param object
         */
        private Message(final int code, final Object object) {
            this.code = code;
            this.object = object;
        }

        /**
         * Equals
         *
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if(o instanceof Message)
                return ((Message) o).code == this.code;
            return super.equals(o);
        }
    }

    // Final Private Variables
    final private ConcurrentLinkedQueue<Message> mMessages = new ConcurrentLinkedQueue<Message>();
    final private Object mLock = new Object();

    /**
     * Post Message
     *
     * @param code
     * @param message
     */
    public void postMessage(final int code, final Object message) {
        mMessages.offer(new Message(code, message));
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    /**
     * Remove Message
     *
     * @param code
     */
    public void removeMessage(final int code) {
        mMessages.remove(new Message(code, null));
    }

    /**
     * Post Message
     *
     * @param code
     */
    public void postMessage(final int code) {
        postMessage(code, null);
    }

    /**
     * Read Message
     *
     * @return
     */
    public Message readMessage() {
        return mMessages.poll();
    }

    /**
     * Wait For Message
     *
     * @param timeInMillis
     */
    public void waitForMessage(final int timeInMillis) throws InterruptedException {
        if(mMessages.size() > 0)
            return;
        synchronized (mLock) {
            mLock.wait(timeInMillis);
        }
    }

    /**
     * Wait For Message
     */
    public void waitForMessage() throws InterruptedException {
        if(mMessages.size() > 0)
            return;
        synchronized (mLock) {
            mLock.wait();
        }
    }
}
