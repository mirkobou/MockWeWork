package ie.mirko;

import java.sql.*;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import java.util.*;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MultiSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Connection connection = null;
        String connectionString = "jdbc:sqlserver://mock1.database.windows.net:1433;"+
                                    "database=MockWeWorkDB1;user=mirko@mock1;password=Nuoro88!;encrypt=true;"+
                                    "trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;"+
                                    "loginTimeout=30;";

        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        final VerticalLayout layout = new VerticalLayout();

        Grid<Rooms> myGrid = new Grid<>();
        myGrid.setWidth("1200px");

        Label headline = new Label(
                "<H1>Marty Party Planners</H1> <p/> <h3>Please enter the details below and click Book</h3>");
        headline.setContentMode(com.vaadin.shared.ui.ContentMode.HTML);

        final TextField name = new TextField();
        name.setCaption("Name of party");

        Slider people = new Slider(0, 200);
        people.setCaption("How many people are invited to this party");
        people.setOrientation(SliderOrientation.HORIZONTAL);
        people.setWidth("500px");
        Button button = new Button("Book");
        final Label vertvalue = new Label();

        people.addValueChangeListener(event -> {
            int value = event.getValue().intValue();
            vertvalue.setValue(String.valueOf(value));
        });

        ComboBox<String> comboBox = new ComboBox<>("Children attending?");
        comboBox.setItems("No", "Yes");

        try {
            connection = DriverManager.getConnection(connectionString);
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM ROOMS_DATA");
            List<Rooms> rm = new ArrayList<Rooms>();

            // While there are more records in the resultset
            while (rs.next()) {
                rm.add(new Rooms(rs.getString("room_name"), rs.getInt("room_capacity"), rs.getString("alcohol"),
                        rs.getString("extras")));
            }
            // Set the items (List)
            myGrid.setItems(rm);
            // Configure the order and the caption of the grid
            myGrid.addColumn(Rooms::getRoom).setCaption("Room");
            myGrid.addColumn(Rooms::getCapacity).setCaption("Capacity");
            myGrid.addColumn(Rooms::getFeature).setCaption("Feature");
            myGrid.addColumn(Rooms::getAlcohol).setCaption("Alcohol Allowed?");

        } catch (Exception e) {
            // This will show an error message if something went wrong
            layout.addComponent(new Label(e.getMessage()));
        }
        Label message = new Label();
        message.setValue("Your party is not booked yet");
        message.setContentMode(ContentMode.HTML);

        myGrid.setSelectionMode(SelectionMode.MULTI);
        MultiSelect<Rooms> select = myGrid.asMultiSelect();
        myGrid.addSelectionListener(event -> {

            Notification.show(select.getValue().stream().map(Rooms::getRoom).collect(Collectors.joining(","))
                    + " were selected");

        });

        button.addClickListener(e -> {
            String aString = select.getValue().stream().map(Rooms::getAlcohol).collect(Collectors.joining(","));
            int cap = select.getValue().stream().mapToInt(Rooms::getCapacity).sum();
            message.setValue(String.valueOf(cap));
            String match = "true";

            if (myGrid.getSelectedItems().size() == 0) {
                message.setValue("<strong>Please select at least one room!</strong>");
            } else if (name.isEmpty()) {
                message.setValue("<strong>Please enter party name.</strong>");
            } else if (comboBox.isEmpty()) {
                message.setValue("<strong>Please confirm if children attending your party</strong>");
            } else if ((comboBox.getValue() == "Yes") && (aString.equalsIgnoreCase(match))) {
                message.setValue(
                        "<strong>You cannot select any rooms serving alcohol if children are attending.</strong>");
            } else if (people.getValue().intValue() > cap) {
                message.setValue("<strong>You have selected rooms with a max capacity of " + cap
                        + " which is not enough to hold </strong>" + people.getValue().intValue());
            } else {
                message.setValue("<strong>Success! The party is booked now</strong>");
            }

        });

        
        Label student = new Label();
        message.setValue("Mirko Busu");
        message.setContentMode(ContentMode.HTML);

        layout.addComponent(headline);
        horizontalLayout.addComponents(name, people, comboBox);
        layout.addComponent(horizontalLayout);
        layout.addComponent(button);

        layout.addComponent(message);
        layout.addComponent(myGrid);
        layout.addComponent(student);
        setContent(layout);
        
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
