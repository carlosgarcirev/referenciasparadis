package org.vaadin.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Route("")
public class MainView extends VerticalLayout {

    private final GreetService dataService;
    private Grid<DataEntry> grid = new Grid<>(DataEntry.class, false);

    @Autowired
    public MainView(GreetService dataService) {
        this.dataService = dataService;
        setSizeFull();
        configureGrid();

        Button addButton = new Button("Nuevo", event -> openCreateDialog());
        Button exportCsvButton = new Button("Exportar CSV", e -> dataService.exportCsv());  // Botón para exportar CSV

        Div buttonsLayout = new Div(addButton, exportCsvButton);  // Añade los botones al layout
        add(buttonsLayout, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addColumn(DataEntry::getMscode).setHeader("MS Code");
        grid.addColumn(DataEntry::getYear).setHeader("Year");
        grid.addColumn(DataEntry::getEstCode).setHeader("Est. Code");
        grid.addColumn(DataEntry::getEstimate).setHeader("Estimate");
        grid.addColumn(DataEntry::getSe).setHeader("SE");
        grid.addColumn(DataEntry::getLowerCIB).setHeader("Lower CIB");
        grid.addColumn(DataEntry::getUpperCIB).setHeader("Upper CIB");
        grid.addColumn(DataEntry::getFlag).setHeader("Flag");
        grid.setSizeFull();

        // Añadir una columna para el botón de acción "Generar"
        grid.addComponentColumn(item -> createGenerateButton(item))
                .setHeader("Generate PDF")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addComponentColumn(item -> createRemoveButton(item)).setHeader("Actions");

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                openEditDialog(event.getValue());
            }
        });
    }

    private Button createRemoveButton(DataEntry item) {
        Button deleteButton = new Button("Eliminar", buttonClickEvent -> {
            Dialog confirmDialog = new Dialog();
            confirmDialog.add(new Span("¿Está seguro de que desea eliminar este elemento?"));

            Button confirmButton = new Button("Confirmar", event -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String userJson = mapper.writeValueAsString(item);
                    dataService.deleteDataItem(userJson);
                    updateList();
                    confirmDialog.close();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });

            Button cancelButton = new Button("Cancelar", event -> confirmDialog.close());

            confirmDialog.add(new Div(confirmButton, cancelButton));
            confirmDialog.open();
        });
        deleteButton.addThemeNames("error", "primary");
        return deleteButton;
    }

    private void updateList() {
        List<DataEntry> users = dataService.getAllDataItems();
        grid.setItems(users);
    }

    private void openCreateDialog() {
        Dialog dialog = new Dialog();
        Binder<DataEntry> binder = new Binder<>(DataEntry.class);

        // Crear campos de texto una sola vez
        TextField mscodeField = createTextField("MS Code");
        TextField yearField = createTextField("Year");
        TextField estCodeField = createTextField("Est. Code");
        TextField estimateField = createTextField("Estimate");
        TextField seField = createTextField("SE");
        TextField lowerCIBField = createTextField("Lower CIB");
        TextField upperCIBField = createTextField("Upper CIB");
        TextField flagField = createTextField("Flag");

        // Vincula cada campo del formulario con su correspondiente propiedad en User
        binder.bind(mscodeField, DataEntry::getMscode, DataEntry::setMscode);
        binder.bind(yearField, DataEntry::getYear, DataEntry::setYear);
        binder.bind(estCodeField, DataEntry::getEstCode, DataEntry::setEstCode);
        binder.forField(estimateField).withConverter(new StringToDoubleConverter("Must be a number")).bind(DataEntry::getEstimate, DataEntry::setEstimate);
        binder.forField(seField).withConverter(new StringToDoubleConverter("Must be a number")).bind(DataEntry::getSe, DataEntry::setSe);
        binder.forField(lowerCIBField).withConverter(new StringToDoubleConverter("Must be a number")).bind(DataEntry::getLowerCIB, DataEntry::setLowerCIB);
        binder.forField(upperCIBField).withConverter(new StringToDoubleConverter("Must be a number")).bind(DataEntry::getUpperCIB, DataEntry::setUpperCIB);
        binder.bind(flagField, DataEntry::getFlag, DataEntry::setFlag);

        FormLayout formLayout = new FormLayout(mscodeField, yearField, estCodeField, estimateField, seField, lowerCIBField, upperCIBField, flagField);

        // Agrega el botón de creación usando el binder
        Button createButton = createCreateButton(dialog, binder);
        formLayout.add(createButton);

        dialog.add(formLayout);
        dialog.open();
    }

    private void openEditDialog(DataEntry user) {
        Dialog dialog = new Dialog();
        Binder<DataEntry> binder = new Binder<>(DataEntry.class);

        // Obtener el usuario actualizado desde el backend
        DataEntry updatedUser = dataService.getUserById(user.get_id());
        if (updatedUser == null) {
            Notification.show("No se encontró el usuario.");
            return;
        }

        binder.setBean(updatedUser);

        FormLayout formLayout = new FormLayout();
        formLayout.add(
                createBoundTextField("MS Code", "mscode", binder),
                createBoundTextField("Year", "year", binder),
                createBoundTextField("Est. Code", "estCode", binder),
                createBoundTextField("Estimate", "estimate", binder),
                createBoundTextField("SE", "se", binder),
                createBoundTextField("Lower CIB", "lowerCIB", binder),
                createBoundTextField("Upper CIB", "upperCIB", binder),
                createBoundTextField("Flag", "flag", binder)
        );

        Button saveButton = new Button("Aceptar", event -> {
            if (binder.writeBeanIfValid(updatedUser)) {
                dataService.updateDataItem(updatedUser);
                dialog.close();
                updateList();
            } else {
                Notification.show("Please check the form fields");
            }
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        formLayout.add(saveButton, cancelButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private TextField createTextField(String label) {
        TextField textField = new TextField(label);
        textField.setClearButtonVisible(true);
        return textField;
    }

    private TextField createBoundTextField(String label, String property, Binder<DataEntry> binder) {
        TextField textField = new TextField(label);
        binder.bind(textField, property);
        return textField;
    }

    private Button createCreateButton(Dialog dialog, Binder<DataEntry> binder) {
        return new Button("Crear", event -> {
            DataEntry newUser = new DataEntry();
            newUser.set_id(UUID.randomUUID()); // Esto está correcto para el ID

            if (binder.writeBeanIfValid(newUser)) {
                dataService.createDataItem(newUser);
                dialog.close();
                updateList();
            } else {
                Notification.show("Please check the form fields");
            }
        });
    }

    private Button createGenerateButton(DataEntry item) {
        Button generateButton = new Button("Generar", buttonClickEvent -> {
            dataService.generatePDF(item.getMscode());
        });
        generateButton.addThemeNames("primary");
        return generateButton;
    }




}
