package trash;

import java.util.ArrayList;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;

public class OpenStreetMaps<Nodee> {
/*
 * 	public Node nearestPoint(double longitude, double latitude) throws Exception {
 
    Node result = null;
    double maxDifference = 0.1;
    ArrayList<Nodee> filteredNodes = getBox(longitude, latitude, maxDifference);

    if ( filteredNodes.size()<=0) return null;
    double minDistance = calcDistance(longitude, latitude,
                    filteredNodes.get(0).lon,filteredNodes.get(0).lon);
    result = filteredNodes.get(0);
    for (int i=1; i<filteredNodes.size();i++){
          Node current = filteredNodes.get(i);
          double diffDistance = calcDistance(longitude, latitude,
                            filteredNodes.get(i).lon,filteredNodes.get(i).lon);
          if( minDistance > diffDistance){
                minDistance = diffDistance;
                result = current;
          }
    }   
    return result;
}

public ArrayList<Nodee> getBox(double lon, double lat, double diff) throws Exception{
    ArrayList<Nodee> inters = getIntersections();
    ArrayList<Nodee> result = new ArrayList<Nodee>();
    for (int i=0; i<inters.size(); i++){
        if(Math.abs(inters.get(i).lat-lat)<=diff &&
           Math.abs(inters.get(i).lon-lon)<=diff)
                result.add(inters.get(i));
    }   
    return result();
}

public double calcDistance(lon1, lat1, lon2, lat2){
    deltaLon = lon1-lon2;
    deltaLat = lat1-lat2;
    return Math.sqrt(deltaLon*deltaLon+deltaLat*deltaLat);
}

*/
}