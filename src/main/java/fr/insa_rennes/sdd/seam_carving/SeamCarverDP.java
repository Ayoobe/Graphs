package fr.insa_rennes.sdd.seam_carving;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;

import fr.insa_rennes.sdd.graph.Coordinate;

public class SeamCarverDP extends SeamCarver {
	
	public SeamCarverDP(Picture picture) {
		super(picture);		
	}
	
	public SeamCarverDP(Picture picture, BiFunction<Double, Double, Double> energyFunction) {
		super(picture, energyFunction);
	}
	
	@Override
	public void reduceToSize(int width, int height) {
		while (width > picture.width())
			cropVertical(verticalSeam());
		while (height > picture.height())
			cropHorizontal(horizontalSeam());
	}

	@Override
	public Deque<Coordinate> horizontalSeam() {		

		double[][] energy = energyMap();
		int w = picture.width();
		int h = picture.height();
		double[][] cost = new double[h][w];
		int[][] from = new int[h][w];

		for (int x = 0; x < w; x++) {
			cost[0][x] = energy[0][x];
		}

		for (int y = 1; y < h; y++) {
			for (int x = 0; x < w; x++) {
				cost[y][x] = energy[y][x] + cost[y - 1][x];
				from[y][x] = x;
				if (x > 0 && cost[y - 1][x - 1] < cost[y][x]) {
					cost[y][x] = energy[y][x] + cost[y - 1][x - 1];
					from[y][x] = x - 1;
				}
				if (x < w - 1 && cost[y - 1][x + 1] < cost[y][x]) {
					cost[y][x] = energy[y][x] + cost[y - 1][x + 1];
					from[y][x] = x + 1;
				}
			}
		}

		double minCost = Double.POSITIVE_INFINITY;
		int minCostX = -1;
		for (int x = 0; x < w; x++) {
			if (cost[h - 1][x] < minCost) {
				minCost = cost[h - 1][x];
				minCostX = x;
			}
		}

		Deque<Coordinate> seam = new ArrayDeque<>();
		for (int y = h - 1, x = minCostX; y >= 0; y--) {
			seam.addFirst(new Coordinate(x, y));
			x = from[y][x];
		}

		return seam;
	}
	
	@Override
	public Deque<Coordinate> verticalSeam() {
		throw new UnsupportedOperationException();
	}
	
}
