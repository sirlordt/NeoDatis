/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.ike;

import java.util.ArrayList;
import java.util.List;

/**
 * @author olivier
 *
 */
public class Flock {
	protected List<Bird> birds;
	
	public Flock(){
		birds = new ArrayList<Bird>();
	}

	public List<Bird> getBirds() {
		return birds;
	}

	public void setBirds(List<Bird> birds) {
		this.birds = birds;
	}
	

}
