package com.clearpath.sa.parser;

import com.clearpath.sa.parser.domain.Person;
import org.beanio.BeanReader;
import org.beanio.InvalidRecordException;
import org.beanio.StreamFactory;
import org.beanio.builder.FieldBuilder;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.RecordBuilder;
import org.beanio.builder.StreamBuilder;

import org.junit.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class StreamBuilderTest {

    private BeanReader beanReader;
    private StreamFactory factory;

    @Before
    public void setup() {
        factory = StreamFactory.newInstance();
        StreamBuilder builder = new StreamBuilder("person")
                .format("fixedlength")
                .parser(new FixedLengthParserBuilder())
                .addRecord(new RecordBuilder("person")
                    .type(Person.class)
                    .minOccurs(1)
                    .addField(new FieldBuilder("name").at(0).length(4))
                    .addField(new FieldBuilder("age").at(5).length(4))
                    .addField(new FieldBuilder("accountNumber").at(14).length(5)));

        factory.define(builder);
    }

    @Test
    public void givenValidInputStringReturned() {
        beanReader = factory.createReader("person", createInput("1111 2222     76543"));

        Object record;
        List<Person> persons = new ArrayList<>();

        // read records from input string
        while ((record = beanReader.read()) != null)
        {
            Person person = (Person) record;
            persons.add(person);
        }
        Assert.assertEquals("1111", persons.get(0).getName());
        Assert.assertEquals("2222", persons.get(0).getAge());
        Assert.assertEquals(Long.valueOf(76543), persons.get(0).getAccountNumber());
    }

    @Test(expected = InvalidRecordException.class)
    public void givenInvalidInputExceptionThrown() {
        beanReader = factory.createReader("person", createInput("2 2222     76543"));
        beanReader.read();
    }

    @After
    public void cleanup() {
        beanReader.close();
    }

    private Reader createInput(String s) {
        return new StringReader(s);
    }

}
