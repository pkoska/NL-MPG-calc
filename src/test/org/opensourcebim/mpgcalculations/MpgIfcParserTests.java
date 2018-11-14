package test.org.opensourcebim.mpgcalculations;

import static org.junit.Assert.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc2x3tc1.IfcRelAssociates;
import org.bimserver.models.ifc2x3tc1.IfcSIPrefix;
import org.eclipse.emf.common.util.BasicEList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opensourcebim.mpgcalculations.MpgIfcParser;

public class MpgIfcParserTests {

	private MpgIfcParser parser;
	IfcMockFactory factory;
	IfcModelInterface ifcModel;
	
	@Before
	public void setUp() throws Exception {
		factory = new IfcMockFactory();
		ifcModel = factory.getModelMock();
		parser = new MpgIfcParser();
	}

	@After
	public void tearDown() throws Exception {
		ifcModel = null;
		parser = null;
	}

	@Test
	public void testNewParserHasNoParsedMaterials() {
		assertEquals(0, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testParseModelWithoutProducts() {
		parser.parseIfcModel(ifcModel);
		assertEquals(0, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testParseModelWithoutMaterialAssociatesWillNotThrowError() {		
		factory.setAssociations(null);
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(0, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testParseModelWithoutGeometryWillNotThrowError() {
		factory.setGeometry(null);
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		
		assertEquals(0, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithSingleMaterial() {
		factory.addMaterial("aluminium");
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		assertEquals(1, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithDuplicateMaterial() {
		factory.addMaterial("aluminium");
		factory.addMaterial("aluminium");
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		// if the materials are the same we should only store it once.
		assertEquals(1, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithMaterialsInDifferentProducts() {
		factory.addMaterial("aluminium");
		factory.addProductToModel(ifcModel);
		
		// first clean the associates to avoid adding aluminium twice.
		factory.setAssociations(new BasicEList<>());
		factory.setGeometry(factory.getGeometryInfoMock(2, 2));
		factory.addMaterial("steel");
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		// if the materials are the same we should only store it once.
		assertEquals(2, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanFindMaterialsThatAreNotLinkedToGeometry() {
		factory.addMaterial("aluminium");		
		factory.addMaterial("steel");
		factory.setGeometry(null);
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		// if the materials are the same we should only store it once.
		assertEquals(2, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithMaterialList() {
		factory.addMaterialList(Arrays.asList("Steel", "Aluminium", "Aluminium"));
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(2, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithEmptyMaterialList() {
		factory.addMaterialList(new ArrayList<String>());
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(0, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithMaterialLayer() {
		factory.addMaterialLayer("brick", 0.15);
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(1, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithMaterialLayerSet() {
		List<Entry<String, Double>> layers = new ArrayList<Entry<String, Double>>();

		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.15));
		layers.add(new AbstractMap.SimpleEntry<>("rockwool", 0.1));
		layers.add(new AbstractMap.SimpleEntry<>("mylar", 0.001));
		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.15));
		
		factory.addMaterialLayerSet(layers);
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(3, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithMaterialLayerSetUsage() {
		List<Entry<String, Double>> layers = new ArrayList<Entry<String, Double>>();

		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.15));
		layers.add(new AbstractMap.SimpleEntry<>("rockwool", 0.1));
		layers.add(new AbstractMap.SimpleEntry<>("mylar", 0.001));
		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.15));
		
		factory.addMaterialLayerSetUsage(layers);
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(3, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanParseModelWithDifferentMaterials() {
		List<Entry<String, Double>> layers = new ArrayList<Entry<String, Double>>();

		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.15));
		layers.add(new AbstractMap.SimpleEntry<>("rockwool", 0.1));
		
		factory.addMaterialLayerSetUsage(layers);
		
		factory.addMaterial("steel");
		factory.addMaterialLayer("styrofoam", .3);
		
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(4, parser.getMpgMaterials().size());
	}
	
	@Test
	public void testCanConvertIfcModelToToBeReportedUnits() {
		// set the units before any model is created. (for that reason we have to reinstantiate the model here.)
		factory.setProjectUnitPrefix(IfcSIPrefix.MILLI);
		ifcModel = factory.getModelMock();
		factory.addMaterial("aluminium");
		
		factory.addProductToModel(ifcModel);
		parser.parseIfcModel(ifcModel);
		assertEquals(1.0e9, parser.getMpgObjectLinks().get(0).getVolume(), 1e-15);
	}
	
	@Test
	public void testParserStoresVolumeOfObjects()
	{
		factory.setGeometry(factory.getGeometryInfoMock(1, 3));
		factory.addMaterial("brick");
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		assertEquals(3, parser.getMpgObjectLinks().get(0).getVolume(), 1e-15);
	}
	
	@Test
	public void testParserDeterminesVolumeOfObjectsPerProduct()
	{
		factory.setGeometry(factory.getGeometryInfoMock(1, 3));
		factory.addMaterial("brick");
		factory.addProductToModel(ifcModel);

		// add another product with a different geometry
		factory.setGeometry(factory.getGeometryInfoMock(1, 2));
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		assertEquals(3, parser.getMpgObjectLinks().get(0).getVolume(), 1e-15);
	}
	
	@Test
	public void testParserDeterminesVolumeOnLayerThicknessRatio()
	{
		factory.setGeometry(factory.getGeometryInfoMock(1, 1));
		factory.addMaterialLayer("brick", 1);
		factory.addMaterialLayer("rockwool", 3);
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		assertEquals(.25, parser.getMpgObjectLinks().get(0).getVolume(), 1e-16);
		assertEquals(.75, parser.getMpgObjectLinks().get(1).getVolume(), 1e-16);
	}
	
	@Test
	public void testParserDeterminesVolumesOfLayersPerProduct()
	{
		factory.setGeometry(factory.getGeometryInfoMock(1, 1));
		factory.addMaterialLayer("brick", 1);
		factory.addMaterialLayer("rockwool", 3);
		factory.addProductToModel(ifcModel);
		
		factory.setGeometry(factory.getGeometryInfoMock(1, 10));
		factory.setAssociations(new BasicEList<IfcRelAssociates>());
		List<Entry<String, Double>> layers = new ArrayList<Entry<String, Double>>();
		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.25));
		layers.add(new AbstractMap.SimpleEntry<>("rockwool", 0.5));
		layers.add(new AbstractMap.SimpleEntry<>("brick", 0.25));
		factory.addMaterialLayerSet(layers);
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		assertEquals(.25, parser.getMpgObjectLinks().get(0).getVolume(), 1e-8);
		assertEquals(.75, parser.getMpgObjectLinks().get(1).getVolume(), 1e-8);

		assertEquals(2.5, parser.getMpgObjectLinks().get(2).getVolume(), 1e-8);
		assertEquals(5.0, parser.getMpgObjectLinks().get(3).getVolume(), 1e-8);
		assertEquals(2.5, parser.getMpgObjectLinks().get(4).getVolume(), 1e-8);
	}
	
	@Test
	public void testParserDeterminesVolumesOfLayersAndNonLayersSeparately()
	{
		factory.setGeometry(factory.getGeometryInfoMock(1, 1));
		factory.addMaterialLayer("brick", 1);
		factory.addMaterialLayer("rockwool", 3);
		factory.addMaterial("steel");
		factory.addProductToModel(ifcModel);
		
		parser.parseIfcModel(ifcModel);
		assertEquals(.125, parser.getMpgObjectLinks().get(0).getVolume(), 1e-8);
		assertEquals(.375, parser.getMpgObjectLinks().get(1).getVolume(), 1e-8);
		assertEquals(.5, parser.getMpgObjectLinks().get(2).getVolume(), 1e-8);
	}
}
