package org.datacite.mds.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.validation.ValidationException;

import org.datacite.mds.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXParseException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/applicationContext.xml")
public class SchemaServiceImplTest {
    
    @Autowired
    SchemaServiceImpl service;
    
    @Before
    public void init() {
        service.schemaLocationLocal = "";
    }
    
    @Test
    public void testGetSchemaLocationNoNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata20();
        String schemaLocation = service.getSchemaLocation(xml);
        assertEquals("http://schema.datacite.org/meta/kernel-2.0/metadata.xsd", schemaLocation);
    }
    
    @Test
    public void testGetSchemaLocationWithNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata21();
        String schemaLocation = service.getSchemaLocation(xml);
        assertEquals("http://schema.datacite.org/meta/kernel-2.1/metadata.xsd", schemaLocation);
    }

    @Test(expected=ValidationException.class)
    public void testGetSchemaLocationNoLocation() throws Exception {
        byte[] xml = "<root/>".getBytes();
        service.getSchemaLocation(xml);
    }
    
    @Test
    public void testGetSchemaNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata21();
        String namespace = service.getNamespace(xml);
        assertEquals("http://datacite.org/schema/kernel-2.1", namespace);
    }

    public void testGetSchemaNamespaceDif() throws Exception {
        byte[] xml = TestUtils.getTestMetadataDif();
        String namespace = service.getNamespace(xml);
        assertEquals("http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/", namespace);
    }

    @Test
    public void testGetSchemaNamespaceNoNamespace() throws Exception {
        byte[] xml = TestUtils.getTestMetadata20();
        String namespace = service.getNamespace(xml);
        assertNull(namespace);
    }

    @Test//(expected=ValidationException.class)
    public void testGetSchemaNamespaceMalformedSchemaLocation() throws Exception {
        String expectedNamespace = "http://example.com";
        byte[] xml = String.format("<root xmlns=\"%s\" xmlns:xsi=\"%s\" xsi:schemaLocation=\"%s\"/>",
                expectedNamespace, service.XSI_NAMESPACE_URI, "foo").getBytes();
        String namespace = service.getNamespace(xml);
        assertEquals(expectedNamespace, namespace);
    }
    
    @Test
    public void getSchemaValidator() throws Exception {
        service.getSchemaValidator("http://schema.datacite.org/meta/kernel-2.1/metadata.xsd");
    }

    @Test(expected = SAXParseException.class)
    public void getSchemaValidatorNonExisting() throws Exception {
        service.getSchemaValidator("file://foobar.xsd");
    }
    
    @Test
    public void testConvertSchemaLocationToLocal() {
        String global = "http://www.gfz-potsdam.de"; //service.schemaLocationPrefix;
        String local = "http://doidb.wdc-terra.org"; //"file://bar/";
        service.schemaLocationLocal = local;
        String converted = service.convertSchemaLocationToLocal(global);
       // assertEquals(local + "/path", converted);
        assertEquals("http://doidb.wdc-terra.org/www.gfz-potsdam.de", converted);
    }
    
    @Test
    public void testConvertSchemaLocationEmptyLocal() {
        String global = service.schemaLocationPrefix;
        service.schemaLocationLocal = "";
        String location = global + "/path";
        String converted = service.convertSchemaLocationToLocal(location);
        assertEquals(location, converted);
    }

}
