package fi.jasoft.dragdroplayouts.demo;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.jasoft.dragdroplayouts.DDAbsoluteLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.details.AbsoluteLayoutTargetDetails;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultAbsoluteLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

public class DragdropLayoutDraggingDemo extends CustomComponent {
    public DragdropLayoutDraggingDemo() {
        setCaption("Dragging layouts");
        setSizeFull();

        final DDAbsoluteLayout layout = new DDAbsoluteLayout();
        layout.setSizeFull();
        setCompositionRoot(layout);

        // Enable dragging components
        layout.setDragMode(LayoutDragMode.CLONE);

        // Enable dropping components
        layout.setDropHandler(new DefaultAbsoluteLayoutDropHandler());

        // Add some components
        layout.addComponent(
                new Label(
                        "This demo shows how you can drag a layout with buttons in it. Try to drag the button group to see what happens"),
                "left:5px;top:5px");

        // A vertical layout which has dragging turned off by default
        VerticalLayout buttons = new VerticalLayout();
        buttons.setSizeUndefined();
        buttons.setCaption("Button group");
        buttons.setSpacing(true);
        buttons.addComponent(new Button("Button1"));
        buttons.addComponent(new Button("Button2"));
        buttons.addComponent(new Button("Button3"));
        buttons.addComponent(new Button("Button4"));

        layout.addComponent(buttons, "left:50px;top:50px");
    }
}
