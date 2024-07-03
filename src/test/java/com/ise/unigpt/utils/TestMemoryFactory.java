package com.ise.unigpt.utils;

import com.ise.unigpt.model.Memory;

public class TestMemoryFactory {
    public static Memory createMemory() throws Exception {
        Memory memory = new Memory(TestHistoryFactory.CreateHistory());
        memory.setId(1);
        ReflectionTestUtils.assertNoNullFields(memory);

        return memory;
    }
}
