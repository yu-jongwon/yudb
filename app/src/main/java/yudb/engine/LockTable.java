package yudb.engine;

import java.util.HashMap;
import java.util.Map;

class LockTable {

  private final static long LOCK_WAIT_TIMEOUT = 1000;
  private final static int X_LOCK = -1;
  private final static LockTable instance = new LockTable();
  private final Map<String, Integer> locks = new HashMap<>();

  private LockTable() { }

  static LockTable getInstance() {
    return instance;
  }

  synchronized void slock(String key) {
    try {
      var start = System.currentTimeMillis();
      while (hasXlock(key) && !isTimedout(start)) {
        wait(LOCK_WAIT_TIMEOUT);
      }
      if (hasXlock(key)) {
        throw new RuntimeException("failed to xlock for " + key);
      }
      locks.put(key, getLockValue(key) + 1);
    } catch (InterruptedException e) {
      throw new RuntimeException("failed to xlock for " + key);
    }
  }

  synchronized void xlock(String key) {
    try {
      var start = System.currentTimeMillis();
      while (hasOtherSlocks(key) && !isTimedout(start)) {
        wait(LOCK_WAIT_TIMEOUT);
      }
      if (hasOtherSlocks(key)) {
        throw new RuntimeException("failed to xlock for " + key);
      }
      locks.put(key, X_LOCK);
    } catch (InterruptedException e) {
      throw new RuntimeException("failed to xlock for " + key);
    }
  }

  synchronized void unlock(String key) {
    if (hasOtherSlocks(key)) {
      locks.put(key, getLockValue(key) - 1);
    } else {
      locks.remove(key);
      notifyAll();
    }
  }

  private int getLockValue(String key) {
    return locks.getOrDefault(key, 0);
  }

  private boolean hasXlock(String key) {
    return getLockValue(key) == X_LOCK;
  }

  private boolean hasOtherSlocks(String key) {
    return getLockValue(key) > 1;
  }

  private boolean isTimedout(long start) {
    return System.currentTimeMillis() - start > LOCK_WAIT_TIMEOUT;
  }

}
