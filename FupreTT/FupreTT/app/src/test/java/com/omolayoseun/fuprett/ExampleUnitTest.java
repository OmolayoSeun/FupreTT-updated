package com.omolayoseun.fuprett;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        main();
    }

    public void main(){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("Hello", "hi");
        hashMap.put("name", "Elizabeth");

        TreeMap<String, HashMap> treeMap = new TreeMap<>();
        treeMap.put("this", hashMap);
        treeMap.put("This2", hashMap);

        System.out.println("Hash Tree:> " + treeMap);
    }
}