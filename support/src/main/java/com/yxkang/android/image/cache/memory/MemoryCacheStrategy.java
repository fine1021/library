package com.yxkang.android.image.cache.memory;

/**
 * MemoryCacheStrategy
 */
public enum MemoryCacheStrategy {
    LRU("LRU"), LFU("LFU"), FIFO("FIFO"), SOFT("SOFT");
    String summary;

    MemoryCacheStrategy(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}
