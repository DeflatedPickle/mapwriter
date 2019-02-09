package mapwriter.tasks;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class Task implements Runnable {
    // the task stores its own future
    private Future<?> future = null;

    // returns false if the task has to be added to a new future
    public abstract boolean checkForDuplicate();

    // methods to access the tasks Future variable
    public final Future<?> getFuture() {
        return this.future;
    }

    public final boolean isDone() {
        return this.future != null && this.future.isDone();
    }

    // called by processTaskQueue after the thread completes
    public abstract void onComplete();

    public final void printException() {
        if (this.future != null) {
            try {
                this.future.get();
            } catch (final ExecutionException e) {
                final Throwable rootException = e.getCause();
                rootException.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    // the method that runs in a separate thread
    // must not access future in run()
    @Override
    public abstract void run();

    public final void setFuture(Future<?> future) {
        this.future = future;
    }
}
