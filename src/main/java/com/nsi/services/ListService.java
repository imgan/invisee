package com.nsi.services;

import java.util.Map;

public interface ListService {
	public Map getListLookupLine(Map map, String lookupHeader);
	public Map getListCountries(Map map);
	public Map getListProvinces(Map map, String version);
	public Map getListCity(Map map);
	public Map getListBank(Map map);
	public Map getListFatca();
	public Map getListRiskProfile();
}
