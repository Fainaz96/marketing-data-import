package com.flaregames.imports.importlog;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.HandleCallback;

public class TransactionDaoMock implements TransactioDao {

    private boolean transactionComplete;

    @Override
    public int dummyForJdbi() {
        return 0;
    }

    @Override
    public void runInTransaction(Runnable runnable) {
        runnable.run();
        transactionComplete = true;
    }

    @Override
    public Handle getHandle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, X extends Exception> R withHandle(HandleCallback<R, X> callback) throws X {
        throw new UnsupportedOperationException();
    }

    public boolean isTransactionComplete() {
        return transactionComplete;
    }
}
