package com.kudzu.android.ibmwatsonlivewallpaper;

import java.util.Random;

public class SpherePoint {
	public double r = 0;
	public double azimuth = 0;
	public double zenith = 0;
	public double x = 0;
	public double y = 0;
	public double z =0;
	
	public SpherePoint clone(){
		SpherePoint n = new SpherePoint();
		n.r=this.r;
		n.azimuth=this.azimuth;
		n.zenith=this.zenith;
		n.x=this.x;
		n.y=this.y;
		n.z=this.z;
		
		return n;
	}
	
	
	public void toCartesian(){
		double sinA = Math.sin(this.azimuth);
		double sinZ = Math.sin(this.zenith);
		double cosA = Math.cos(this.azimuth);
		double cosZ = Math.cos(this.zenith);
		
		this.x = this.r*sinZ*cosA;
		this.y = this.r*sinZ*sinA;
		this.z = this.r*cosZ;
		
	}
	
	public void ToSpherical(){
		double xsq = this.x * this.x;
		double ysq = this.y * this.y;
		double zsq = this.z * this.z;
		
	    this.r = Math.sqrt(xsq+ysq+zsq);
		

	    if (this.r <= 0) {
	      this.azimuth = 0;
	      this.zenith = 90;
	    } else {
	      this.azimuth = Math.atan2(this.y,this.x);
	      this.zenith = Math.acos(this.z/this.r);
	    }
	}
	
	
	public void RotateXZ(double a){
		double xzr = Math.sqrt(this.x * this.x + this.z * this.z);
		double ang = Math.atan2(this.z,this.x);
	    ang += a;    // add the rotation
	    // convert back to rectangular
	    this.x = xzr * Math.cos(ang);
	    this.z = xzr * Math.sin(ang);
	}
	
	public double DistanceS(SpherePoint p){
		double dx = p.x - this.x;
		double dy = p.y - this.y;
		double dz = p.z - this.z;
	    return dx*dx + dy*dy + dz*dz;
	}
	
	public void Normalise(Double new_r){
		double d = Math.sqrt(this.x*this.x+this.y*this.y+this.z*this.z);
	    if (d > 0) {
	        new_r = new_r/d;
	        this.x *= new_r;
	        this.y *= new_r;
	        this.z *= new_r;
	      }
	}
	
	
	public void Random(Double r,Random generator){
	    this.r = r;
	    this.zenith = generator.nextInt(181);
	    this.azimuth = generator.nextInt(361);
	    this.toCartesian();  
	}
}
