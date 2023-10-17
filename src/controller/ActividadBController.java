package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import dao.DAOPersonas;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Persona;

public class ActividadBController implements Initializable {
	
	private ResourceBundle bundle;

    @FXML
    private Button btnAgregarPersona;
    
    @FXML
    private Button btnEliminarPersona;

    @FXML
    private Button btnModificarPersona;

    @FXML
    private TableColumn<Persona, String> columnaApellidos;

    @FXML
    private TableColumn<Persona, Integer> columnaEdad;

    @FXML
    private TableColumn<Persona, String> columnaNombre;

    @FXML
    private TableView<Persona> tablaPersonas;

    @FXML
    private TextField tfApellidos;

    @FXML
    private TextField tfEdad;

    @FXML
    private TextField tfNombre;
    
    @FXML
    private TextField tfBusqueda;
    
    @FXML
    private Button btnExportar;

    @FXML
    private Button btnImportar;
    

    @FXML
    void buscar(KeyEvent event) {
    	
    	String texto = tfBusqueda.getText().toLowerCase();
    	if (texto != null && !texto.isBlank()) {    		
    		List<Persona> listaFiltrada = DAOPersonas.getPersonas().stream().filter(pers -> pers.getNombre().toLowerCase().contains(texto)).toList();
    		tablaPersonas.setItems(FXCollections.observableArrayList(listaFiltrada));
    		tablaPersonas.refresh();
    	} else {
    		mostrarTodasLasPersonas();
    	}
    }


    @FXML
    void agregarPersona(ActionEvent event) {
    	
    	sacarDialogo((panel, controller) -> {});
    	
    }
    
    @FXML
    void modifcarPersona(ActionEvent event) {
    	
    	Persona persona = tablaPersonas.getSelectionModel().getSelectedItem();
    	
    	if (persona != null) {    		
    		sacarDialogo((panel, con) -> {
    			con.setPersona(persona);
    			TextField nom = (TextField) panel.lookup("#tfNombre");
    			TextField ap = (TextField) panel.lookup("#tfApellidos");
    			TextField ed = (TextField) panel.lookup("#tfEdad");
    			
    			
    			nom.setText(persona.getNombre());
    			ap.setText(persona.getApellidos());
    			ed.setText(Integer.toString(persona.getEdad()));
    		});
    	}
    	
    	
    }
    
    @FXML
    void eliminarPersona(ActionEvent event) {
    	Persona persona = tablaPersonas.getSelectionModel().getSelectedItem();
    	if (persona != null) {
    		try {
				if (DAOPersonas.eliminarPersona(persona) > 0) {					
					tablaPersonas.getItems().remove(persona);
					tablaPersonas.refresh();
					Alert alert = new Alert(AlertType.INFORMATION, "La persona \"" + persona.getNombre() + "\" fue eliminada", ButtonType.OK);
					alert.showAndWait();
				}
			} catch (SQLException e) {
	    		Alert alert = new Alert(AlertType.ERROR, "No se pudo eliminar la persona de la base de datos", ButtonType.OK);
	    		alert.showAndWait();
			}
    	}
    }

    


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
		
		columnaApellidos.setCellValueFactory(param -> {
			Persona persona = param.getValue();
			if (persona != null && persona.getApellidos() != null) {					
				return new SimpleStringProperty(persona.getApellidos());
			}
			return new SimpleStringProperty("");
		});
		
		columnaNombre.setCellValueFactory(param -> {
			Persona persona = param.getValue();
			if (persona != null && persona.getNombre() != null) {					
				return new SimpleStringProperty(persona.getNombre());
			}
			return new SimpleStringProperty("");
		});
		
		columnaEdad.setCellValueFactory(param -> {
			Persona persona = param.getValue();
			if (persona.getEdad() >= 0) {
				return new SimpleIntegerProperty(persona.getEdad()).asObject();
			}
			return  new SimpleIntegerProperty().asObject();
		});
		
		tablaPersonas.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Persona>() {

			@Override
			public void onChanged(Change<? extends Persona> c) {
				Persona persona = tablaPersonas.getSelectionModel().getSelectedItem();
				if (persona != null ) {
					if (tfNombre != null) {
						
						tfNombre.setText(persona.getNombre());
					}
					if (tfApellidos != null) {						
						tfApellidos.setText(persona.getApellidos());
					}
					if (tfEdad != null) {						
						tfEdad.setText(persona.getEdad() >= 0 ? Integer.toString(persona.getEdad()) : "");
					}
				}
			}
			
		});
		
		cargarPersonasDeBD();
		
		
	}
	


	@FXML
    void exportar(ActionEvent event) {
    	StringBuilder sb = new StringBuilder("Nombre,Apellidos,Edad\n");
    	Iterable<Persona> iterable = tablaPersonas.getItems();
    	iterable.forEach(per -> sb.append(String.format("%s,%s,%d\n", per.getNombre(), per.getApellidos(), per.getEdad())));
    	
    	FileChooser fc = new FileChooser();
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	fc.setInitialFileName("personas.csv");
    	
        //FILTRO DE EXTENSIONES
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(extFilter);
    	
    	//COGEMOS EL WINDOW DESDE LA PROPIA FUENTE DEL EVENTO
    	File seleccion = fc.showSaveDialog(((Node)event.getSource()).getScene().getWindow());
    	
    	try (FileWriter fw = new FileWriter(seleccion)) {    		
    		fw.write(sb.toString());
    	} catch (IOException e) {
    		Alert alert = new Alert(AlertType.ERROR, "No se pudo exportar", ButtonType.OK);
    		alert.showAndWait();
		} 
    	
    	
    }

    @FXML
    void importar(ActionEvent event) {
    	//CREAR EL FILECHOOSER
    	FileChooser fc = new FileChooser();
    	fc.setInitialDirectory(new File(System.getProperty("user.home")));
    	
        //FILTRO DE EXTENSIONES
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(extFilter);
        
        //Window desde la fuente del evento
        File archivo = fc.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        	//NOS SALTAMOS LA PRIMERA LÍNEA PUESTO QUE ES LA CABECERA
        	String linea = br.readLine();
        	Set<Persona> importadas = new LinkedHashSet<>();
        	while ((linea = br.readLine()) != null) {
        		String[] valores = linea.split(",");
        		if (valores.length == 3) {
        			importadas.add(new Persona(valores[0], valores[1], Integer.parseInt(valores[2])));
        		} else {
        			throw new IOException("FORMATO INCORRECTO");        			
        		}
        	}
        	DAOPersonas.anadirPersona(importadas.toArray(Persona[]::new));
        	//AL RECORRER TODAS LAS TUPLAS -> REDIBUJAR LA LISTA CON TODOS LOS VALORES Y RESETEAR LA BÚSQUEDA
        	mostrarTodasLasPersonas();
        } catch (IOException | NumberFormatException e) {
			e.printStackTrace();
    		Alert alert = new Alert(AlertType.ERROR, "Hubo un problema al abrir el fichero", ButtonType.OK);
    		alert.showAndWait();
		} catch (SQLException e) {
    		Alert alert = new Alert(AlertType.ERROR, "No se pudieron añadir las personas importadas a la base de datos", ButtonType.OK);
    		alert.showAndWait();
		}
        
    	
    }
    
    private void cargarPersonasDeBD() {
    	tablaPersonas.getItems().addAll(DAOPersonas.getPersonas());
    }
    
    private void mostrarTodasLasPersonas() {
    	tfBusqueda.setText("");
    	tablaPersonas.setItems(FXCollections.observableArrayList(DAOPersonas.getPersonas()));
    	tablaPersonas.refresh();
    }
	
	@FunctionalInterface
	static interface Callback {
		void run(GridPane panel, AgregarPersonaController controller);
	}
	
	private void sacarDialogo(Callback callback) {
		try {
			Stage stage = new Stage();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AgregarPersona.fxml"), bundle);
			GridPane rootPersona = loader.load();
			AgregarPersonaController controller = loader.getController();
			controller.pasarTabla(tablaPersonas);
			
			if(callback != null) {				
				callback.run(rootPersona, controller);
			}
			
			
			Scene scene = new Scene(rootPersona);
			stage.setResizable(false);
			stage.setTitle("Agregar Persona");
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
