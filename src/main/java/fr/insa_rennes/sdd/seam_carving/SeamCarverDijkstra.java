package fr.insa_rennes.sdd.seam_carving;

import java.util.Deque;
import java.util.function.BiFunction;
import fr.insa_rennes.sdd.dijkstra.Dijkstra;
import fr.insa_rennes.sdd.graph.Coordinate;
import fr.insa_rennes.sdd.graph.LeftToRightGridGraph;
import fr.insa_rennes.sdd.graph.TopToBottomGridGraph;

public class SeamCarverDijkstra extends SeamCarver {
	
	public SeamCarverDijkstra(Picture picture) {
		super(picture);		
	}
	
	public SeamCarverDijkstra(Picture picture, BiFunction<Double, Double, Double> energyFunction) {
		super(picture, energyFunction);
	}
	
	@Override
	public void reduceToSize(int width, int height) {
		while (picture.width() > width) {
			cropVertical(verticalSeam());
		}
		while (picture.height() > height) {
			cropHorizontal(horizontalSeam());
		}
	}
		
	@Override
	public Deque<Coordinate> horizontalSeam() {
		TopToBottomGridGraph g = new TopToBottomGridGraph(this.energyMap());
		Dijkstra<Coordinate> dj = new Dijkstra<Coordinate>(g, Coordinate.BOTTOM);
		return dj.getPathTo(Coordinate.TOP);
	}
	
	@Override
	public Deque<Coordinate> verticalSeam() {
		LeftToRightGridGraph g = new LeftToRightGridGraph(this.energyMap());
		Dijkstra<Coordinate> dj = new Dijkstra<Coordinate>(g, Coordinate.LEFT);
		return dj.getPathTo(Coordinate.RIGHT);
 	}
	
}
