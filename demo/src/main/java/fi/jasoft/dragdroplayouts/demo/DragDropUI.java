package fi.jasoft.dragdroplayouts.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.Reindeer;

import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.JavaSourceConversionOptions;
import de.java2html.util.IllegalConfigurationException;

@Theme("demo")
@Widgetset("fi.jasoft.dragdroplayouts.demo.DemoWidgetSet")
public class DragDropUI extends UI {

    private final Label code = new Label("No source code available.",
            ContentMode.HTML);

    private final ObjectProperty<Component> currentView = new ObjectProperty<Component>(
            null, Component.class);
        
    private final ListSelect componentList = new ListSelect();

    private final VerticalSplitPanel split = new VerticalSplitPanel();

    public DragDropUI() {
        componentList.setNullSelectionAllowed(false);
        componentList.addContainerProperty("caption", String.class, "");
        componentList.setWidth("250px");
        componentList.setHeight("100%");
        componentList.setImmediate(true);
        componentList.setPropertyDataSource(currentView);
        componentList
        .addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                        Component view = (Component) event.getProperty()
                                .getValue();
                        Page.getCurrent().setUriFragment(
                                view.getCaption().replaceAll(" ", ""));
            }
        });
        componentList.setItemCaptionPropertyId("caption");
        componentList.setItemCaptionMode(ItemCaptionMode.PROPERTY);

        addDemo(new DragdropAbsoluteLayoutDemo());
        addDemo(new DragdropVerticalLayoutDemo());
        addDemo(new DragdropHorizontalLayoutDemo());
        addDemo(new DragdropGridLayoutDemo());
        addDemo(new DragdropCssLayoutDemo());
        addDemo(new DragdropFormLayoutDemo());

        addDemo(new DragdropLayoutDraggingDemo());
        addDemo(new DragdropHorizontalSplitPanelDemo());
        addDemo(new DragdropVerticalSplitPanelDemo());
        addDemo(new DragdropTabsheetDemo());
        addDemo(new DragdropAccordionDemo());

        addDemo(new DragdropDragFilterDemo());
        addDemo(new DragdropCaptionModeDemo());

        addDemo(new DragdropIframeDragging());

    }

    private void addDemo(CustomComponent view) {
        Item item = componentList.addItem(view);
        item.getItemProperty("caption").setValue(view.getCaption());
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout root = new VerticalLayout();
        root.setStyleName(Reindeer.LAYOUT_BLUE);
        root.setSizeFull();
        root.setSpacing(true);
        root.setMargin(true);
        setContent(root);

        Label header = new Label("DragDropLayouts for Vaadin 7");
        header.setStyleName(Reindeer.LABEL_H1);
        root.addComponent(header);

        Panel subroot = new Panel(new HorizontalLayout());
        ((HorizontalLayout) subroot.getContent()).setSizeFull();
        subroot.setSizeFull();

        ((HorizontalLayout) subroot.getContent()).addComponent(componentList);
        root.addComponent(subroot);
        root.setExpandRatio(subroot, 1);

        split.setSizeFull();
        split.setSplitPosition(30, Unit.PERCENTAGE, true);
        split.setStyleName(Reindeer.SPLITPANEL_SMALL);
        ((HorizontalLayout) subroot.getContent()).addComponent(split);
        ((HorizontalLayout) subroot.getContent()).setExpandRatio(split, 1);

        Panel codePanel = new Panel();
        codePanel.setSizeFull();
        codePanel.setContent(code);
        split.setSecondComponent(codePanel);

        componentList.select(componentList.getItemIds().iterator().next());

        getPage().addUriFragmentChangedListener(
                new UriFragmentChangedListener() {

                    @Override
                    public void uriFragmentChanged(UriFragmentChangedEvent event) {
                        String viewCaption = event.getUriFragment();

                        for (Object id : componentList.getItemIds()) {
                            String caption = componentList.getItemCaption(id)
                                    .replaceAll(" ", "");
                            if (caption.equals(viewCaption)) {
                                try {
                                    tabChanged((Component) id);
                                    currentView.setValue((Component) id);
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

    }

    private void tabChanged(Component tab) throws IOException {
        split.setFirstComponent(tab);

        String path = tab.getClass().getCanonicalName().replaceAll("\\.", "/")
                + ".java";

        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            code.setValue("No source code available.");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder codelines = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
        	if (line.startsWith("import")) {
                // Remove imports
            } else if (line.startsWith("package")) {
                // Remove package declaration
            } else {
                codelines.append(line);
                codelines.append("\n");
            }

            line = reader.readLine();
        }

        reader.close();

        String code = codelines.toString().trim();
        
        code = Pattern
                .compile("public String getCodePath.*?}",
                        Pattern.MULTILINE | Pattern.DOTALL).matcher(code)
                        .replaceAll("");

             
        this.code.setValue(getFormattedSourceCode(code));
    }
    
    public String getFormattedSourceCode(String sourceCode) {
        try {
            JavaSource source = new JavaSourceParser().parse(new StringReader(sourceCode));
            JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter();
            StringWriter writer = new StringWriter();
            JavaSourceConversionOptions options = JavaSourceConversionOptions.getDefault();
            options.setShowLineNumbers(true);
            options.setAddLineAnchors(false);
            converter.convert(source, options, writer);

            return writer.toString();
        }
        catch (IllegalConfigurationException exception) {
            return sourceCode;
        }
        catch (IOException exception) {
            return sourceCode;
        }
    }
}
