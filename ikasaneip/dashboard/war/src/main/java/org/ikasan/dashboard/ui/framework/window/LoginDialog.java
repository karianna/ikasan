package org.ikasan.dashboard.ui.framework.window;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.data.LoginFieldGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.security.service.AuthenticationService;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * 
 * @author CMI2 Development Team
 *
 */
public class LoginDialog extends Window
{
    private static final long serialVersionUID = 2394313614920487219L;
    
    /** Logger instance */
    private static Logger logger = Logger.getLogger(LoginDialog.class);

    /**
     * Constructor
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param commitHandler
     */
    public LoginDialog(AuthenticationService authenticationService,
            VisibilityGroup visibilityGroup,
            NavigationPanel commitHandler)
    {
        super("Login");
        init(authenticationService, visibilityGroup, commitHandler);
    }

    /**
     * Helper method to initialise this object.
     * 
     * @param userService
     * @param authProvider
     * @param visibilityGroup
     * @param userDetailsHelper
     * @param commitHandler
     */
    protected void init(AuthenticationService authenticationService,
            VisibilityGroup visibilityGroup, final NavigationPanel commitHandler)
    {
        super.setModal(true);
        super.setResizable(false);
        super.center();
        super.setStyleName("ikasan");

        PropertysetItem item = new PropertysetItem();
        item.addItemProperty(LoginFieldGroup.USERNAME, new ObjectProperty<String>(""));
        item.addItemProperty(LoginFieldGroup.PASSWORD, new ObjectProperty<String>(""));
        
        FormLayout form = new FormLayout();
        form.setWidth("280px");
        form.setHeight("140px");
        form.setMargin(true);
        
        final TextField userNameField = new TextField("Username");
        userNameField.addValidator(new StringLengthValidator(
            "The username must not be empty",
            1, null, true));
        userNameField.setValidationVisible(false);
        userNameField.setStyleName("ikasan");
        form.addComponent(userNameField);

        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setStyleName("ikasan");
        passwordField.addValidator(new StringLengthValidator(
            "The password must not be empty",
            1, null, true));
        passwordField.setValidationVisible(false);
        form.addComponent(passwordField);

        final LoginFieldGroup binder = new LoginFieldGroup(item, visibilityGroup
            , authenticationService);
        binder.bind(userNameField, LoginFieldGroup.USERNAME);
        binder.bind(passwordField, LoginFieldGroup.PASSWORD);

        HorizontalLayout buttons = new HorizontalLayout();
        
        Button loginButton = new Button("Login");
        loginButton.setStyleName(Reindeer.BUTTON_SMALL);
        loginButton.addClickListener(new ClickListener() 
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                try 
                {
                    userNameField.validate();
                    passwordField.validate();
                } catch (InvalidValueException e) {
                    userNameField.setValidationVisible(true);
                    passwordField.setValidationVisible(true);
                    return;
                }

                try 
                {
                    binder.commit();
                    userNameField.setValue("");
                    passwordField.setValue("");
                    close();
                    commitHandler.postCommit();
                } catch (CommitException e) 
                {
                    Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        buttons.addComponent(loginButton);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(Reindeer.BUTTON_SMALL);
        cancelButton.addClickListener(new ClickListener() 
        {
            @Override
            public void buttonClick(ClickEvent event) 
            {
                close();
                binder.discard();
            }
        });
        buttons.addComponent(cancelButton);

        form.addComponent(buttons);
        this.setContent(form);
    }
}
