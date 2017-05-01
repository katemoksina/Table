package com.example.kate.table;

import org.junit.Test;


import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
//    @Test
//    public void password_hashing_isCorrect() throws Exception {
//        assertEquals(BookingActivity.hashPassword("1234").toString(),"$2a$12$d8cOIJcN5jT9XVS9KPSs6eIHWIxIYZwhklpuzpAHO0tos9m.nJhXO");
//    }
    @Test
    public void password_validation_isCorrect() throws Exception {
        assert(CancelActivity.checkPassword("1234","$2a$12$d8cOIJcN5jT9XVS9KPSs6eIHWIxIYZwhklpuzpAHO0tos9m.nJhXO"));
    }

}
