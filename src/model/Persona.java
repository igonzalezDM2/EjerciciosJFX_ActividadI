package model;

import java.util.Objects;

public class Persona {
	private String nombre, apellidos;
	private int edad, id;
	public Persona(String nombre, String apellidos, int edad) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.edad = edad;
	}
	public String getNombre() {
		return nombre;
	}
	public Persona setNombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	public String getApellidos() {
		return apellidos;
	}
	public Persona setApellidos(String apellidos) {
		this.apellidos = apellidos;
		return this;
	}
	public int getEdad() {
		return edad;
	}
	public Persona setEdad(int edad) {
		this.edad = edad;
		return this;
	}
	public int getId() {
		return id;
	}
	public Persona setId(int id) {
		this.id = id;
		return this;
	}
	@Override
	public int hashCode() {
		return Objects.hash(apellidos, edad, nombre);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Persona other = (Persona) obj;
		return Objects.equals(apellidos, other.apellidos) && edad == other.edad && Objects.equals(nombre, other.nombre);
	}
	
}
