package org.opensourcebim.nmd;

import java.util.ArrayList;
import java.util.List;

import org.opensourcebim.ifccollection.MpgMaterial;
import org.opensourcebim.ifccollection.MpgObjectStore;

public class NmdDataResolverImpl implements NmdDataResolver {

	private List<NmdDataService> services;
	private EditableDataService editor;
	
	public NmdDataResolverImpl() {
		services = new ArrayList<NmdDataService>();
	}
	
	@Override
	public MpgObjectStore NmdToMpg(MpgObjectStore ifcResults) {

		MpgObjectStore nmdResults = ifcResults;
		
		try
        { 
			// start any subscribed services
			for (NmdDataService nmdDataService : services) {
				nmdDataService.start();
			}
			
			// get data per material - run through services in order
			for (MpgMaterial material : ifcResults.getMaterials().values()) {
				MpgMaterial nmdMaterial = tryGetMaterialProperties(material);
				
			}
			
        } 
        catch(ArrayIndexOutOfBoundsException ex) 
        { 
            System.out.println("Error occured in retrieving material data");
        } 
          
        finally
        { 
        	for (NmdDataService nmdDataService : services) {
    			nmdDataService.stop();
    		}
        } 
		
		return nmdResults;
	}

	private MpgMaterial tryGetMaterialProperties(MpgMaterial material) {
		MpgMaterial retrievedMaterial = null;
		for (NmdDataService nmdDataService : services) {
			retrievedMaterial = nmdDataService.retrieveMaterial(material);
			if (retrievedMaterial != null) {
				break;
			}
		}
		
		return retrievedMaterial;
	}
	
	@Override
	public void addService(NmdDataService nmdDataService) {
		// check if same service is not already present?
		services.add(nmdDataService);
		
		if(nmdDataService instanceof EditableDataService) {
			editor = (EditableDataService)nmdDataService;
		}
	}

}