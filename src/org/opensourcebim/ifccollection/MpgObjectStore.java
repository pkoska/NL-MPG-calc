package org.opensourcebim.ifccollection;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface MpgObjectStore {

	public HashMap<String, MpgMaterial> getMaterials();
	public List<MpgObjectGroup> getObjectGroups();
	public List<MpgObject> getSpaces();
	public void Reset();
	
	public void addObjectGroup(MpgObjectGroup group);
	public List<String> getDistinctProductTypes();
	public List<MpgObjectGroup> getObjectsByProductType(String productType);
	public List<MpgObjectGroup> getObjectsByProductName(String productName);
	public List<MpgObject> getObjectsByMaterialName(String materialName);
	
	public void addMaterial(String string);
	public Set<String> getAllMaterialNames();
	public MpgMaterial getMaterialByName(String name);
	public List<MpgMaterial> getMaterialsByProductType(String productType);
	public Double GetTotalVolumeOfMaterial(String name);
	public Double GetTotalVolumeOfProductType(String productType);
	
	public void addSpace(MpgObject space);
	Double getTotalFloorArea();
	
	boolean CheckForWarningsAndErrors();
	List<String> getOrphanedMaterials();
	List<String> getObjectGUIDsWithoutMaterial();
	List<String> getObjectGuidsWithPartialMaterialDefinition();
	
	public void FullReport();
	public void SummaryReport();



}
