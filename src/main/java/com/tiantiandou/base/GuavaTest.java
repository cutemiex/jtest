package com.tiantiandou.base;

import java.util.Iterator;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/***
 * 类说明: google guava 的test用例和学习.
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月22日
 */
public final class GuavaTest {
    private static final Logger LOGGER = LoggerFactory.getLogger("GuavaTest");

    private GuavaTest() {

    }

    public static void testCache() {

    }

    public static void testConcurrent() {

    }

    public static void testBase() throws Exception {
        LOGGER.debug(Objects.toStringHelper(GuavaTest.class).add("name", "tommy")
                .add("score", new Random().nextInt()).toString());

        String ori = "test1  ||  nulll|\"||||da|test2|";
        Iterable<String> vs = Splitter.on("|").omitEmptyStrings().trimResults().split(ori);
        Iterator<String> iter = vs.iterator();
        while (iter.hasNext()) {
            LOGGER.debug(iter.next());
        }

        StringBuffer buf = new StringBuffer();
        Joiner.on("###").skipNulls().appendTo(buf, vs.iterator());
        LOGGER.debug("joiner message is %s : " + buf);
    }

    public static void testNet() {

    }

    public static void testCollection() {
        Multimap<String, String> mmap = ArrayListMultimap.create();
        mmap.put("key1", "tommy");
        mmap.put("key1", "tommy");
        mmap.put("key1", null);
        LOGGER.debug("HashMap unique value support is  {}", mmap.get("key1").size() == 2);
        mmap = HashMultimap.create();
        mmap.put("key1", "tommy");
        mmap.put("key1", "tommy");
        mmap.put("key1", null);
        LOGGER.debug("HashMap unique and null value support is  {}", mmap.get("key1").size() == 2);

        BiMap<String, String> bimap = HashBiMap.create();
        bimap.put("key1", "val1");
        LOGGER.debug("bimap value for val1 is  {}", bimap.inverse().get("val1"));
    }

    public static void testReflection() {

    }

    public static void main(String[] args) throws Exception {
        testBase();
        testCollection();
    }
}
