package controller;

import static dao.DAOPersonas.anadirPersona;
import static dao.DAOPersonas.existe;
import static dao.DAOPersonas.updatePersona;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Persona;

public class AgregarPersonaController {
	
	private Persona persona;
	
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	TableView<Persona> tabla;
	
	public void pasarTabla(TableView<Persona> tabla) {
		this.tabla = tabla;
	}

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private TextField tfApellidos;

    @FXML
    private TextField tfEdad;

    @FXML
    private TextField tfNombre;

    @FXML
    void cancelar(ActionEvent event) {
        cerrar(event);
    }

    @FXML
    void guardar(ActionEvent event) {
    	Persona persona = recogerPersona();
    	if (validarPersona(persona) && insertarPersona(persona)) {
            cerrar(event);
    	}
    	
    	
    }
    
    private void cerrar(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    private boolean insertarPersona(Persona persona) {
    	Persona p1 = getPersona();
    	if (p1 != null) {
    		try {
    			//Si sale bien, modifica p1 (la persona seleccionada en la tabla)
				updatePersona(p1, persona.getNombre(), persona.getApellidos(), persona.getEdad());
				tabla.refresh();
				return true;
			} catch (SQLException e) {
	    		Alert alert = new Alert(AlertType.ERROR, "No se pudo modificar la persona en la base de datos", ButtonType.OK);
	    		alert.showAndWait();
			}
    	} else if (!existe(persona)) {    		
    		try {
				anadirPersona(persona);
				tabla.getItems().add(persona);
				tabla.refresh();
				return true;
			} catch (SQLException e) {
	    		Alert alert = new Alert(AlertType.ERROR, "No se pudo añadir la persona a la base de datos", ButtonType.OK);
	    		alert.showAndWait();
			}
    	} else {
    		Alert alert = new Alert(AlertType.WARNING, "La persona está repetida", ButtonType.OK);
    		alert.showAndWait();
    	}
    	return false;
	}
    
    private Persona recogerPersona() {
    	String nombre = tfNombre.getText() != null ? tfNombre.getText().toString() : "";
    	String apellidos = tfApellidos.getText() != null ? tfApellidos.getText().toString() : "";
    	int edad = tfEdad.getText() != null ? parseInt(tfEdad.getText().toString()) : -1;
    	return new Persona(nombre, apellidos, edad);
    }

    
    private boolean validarPersona(Persona persona) {
    	StringBuilder sb = new StringBuilder();
    	
    	if (persona.getNombre() == null || persona.getNombre().isBlank()) {
    		sb.append("El campo Nombre es obligatorio\n");
    	}
    	if (persona.getApellidos() == null || persona.getApellidos().isBlank()) {
    		sb.append("El campo Apellidos es obligatorio\n");    		
    	}
    	if (persona.getEdad() < 0) {
    		sb.append("El campo Edad es obligatorio\n");
    	}
    	
    	if (!sb.isEmpty()) {
    		Alert alert = new Alert(AlertType.ERROR, sb.toString(), ButtonType.OK);
    		alert.showAndWait();
    		return false;
    	}
    	return true;
    }
    
	private static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return -1;
		} catch (NullPointerException e1) {
			return -1;
		}
	}

}
