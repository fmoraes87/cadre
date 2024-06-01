import { NgModule } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { HashLocationStrategy, LocationStrategy } from "@angular/common";
import { AppRoutingModule } from "./app-routing.module";

import { AccordionModule } from "primeng/accordion";
import { AutoCompleteModule } from "primeng/autocomplete";
import { AvatarModule } from "primeng/avatar";
import { AvatarGroupModule } from "primeng/avatargroup";
import { BadgeModule } from "primeng/badge";
import { BreadcrumbModule } from "primeng/breadcrumb";
import { ButtonModule } from "primeng/button";
import { CalendarModule } from "primeng/calendar";
import { CardModule } from "primeng/card";
import { CarouselModule } from "primeng/carousel";
import { CascadeSelectModule } from "primeng/cascadeselect";
import { ChartModule } from "primeng/chart";
import { CheckboxModule } from "primeng/checkbox";
import { ChipModule } from "primeng/chip";
import { ChipsModule } from "primeng/chips";
import { CodeHighlighterModule } from "primeng/codehighlighter";
import { ConfirmDialogModule } from "primeng/confirmdialog";
import { ConfirmPopupModule } from "primeng/confirmpopup";
import { ColorPickerModule } from "primeng/colorpicker";
import { ContextMenuModule } from "primeng/contextmenu";
import { DataViewModule } from "primeng/dataview";
import { DialogModule } from "primeng/dialog";
import { DividerModule } from "primeng/divider";
import { DropdownModule } from "primeng/dropdown";
import { FieldsetModule } from "primeng/fieldset";
import { FileUploadModule } from "primeng/fileupload";
import { FullCalendarModule } from "primeng/fullcalendar";
import { GalleriaModule } from "primeng/galleria";
import { InplaceModule } from "primeng/inplace";
import { InputNumberModule } from "primeng/inputnumber";
import { InputMaskModule } from "primeng/inputmask";
import { InputSwitchModule } from "primeng/inputswitch";
import { InputTextModule } from "primeng/inputtext";
import { InputTextareaModule } from "primeng/inputtextarea";
import { KnobModule } from "primeng/knob";
import { LightboxModule } from "primeng/lightbox";
import { ListboxModule } from "primeng/listbox";
import { MegaMenuModule } from "primeng/megamenu";
import { MenuModule } from "primeng/menu";
import { MenubarModule } from "primeng/menubar";
import { MessagesModule } from "primeng/messages";
import { MessageModule } from "primeng/message";
import { MultiSelectModule } from "primeng/multiselect";
import { OrderListModule } from "primeng/orderlist";
import { OrganizationChartModule } from "primeng/organizationchart";
import { OverlayPanelModule } from "primeng/overlaypanel";
import { PaginatorModule } from "primeng/paginator";
import { PanelModule } from "primeng/panel";
import { PanelMenuModule } from "primeng/panelmenu";
import { PasswordModule } from "primeng/password";
import { PickListModule } from "primeng/picklist";
import { ProgressBarModule } from "primeng/progressbar";
import { RadioButtonModule } from "primeng/radiobutton";
import { RatingModule } from "primeng/rating";
import { RippleModule } from "primeng/ripple";
import { ScrollPanelModule } from "primeng/scrollpanel";
import { ScrollTopModule } from "primeng/scrolltop";
import { SelectButtonModule } from "primeng/selectbutton";
import { SidebarModule } from "primeng/sidebar";
import { SkeletonModule } from "primeng/skeleton";
import { SlideMenuModule } from "primeng/slidemenu";
import { SliderModule } from "primeng/slider";
import { SplitButtonModule } from "primeng/splitbutton";
import { SplitterModule } from "primeng/splitter";
import { StepsModule } from "primeng/steps";
import { TabMenuModule } from "primeng/tabmenu";
import { TableModule } from "primeng/table";
import { TabViewModule } from "primeng/tabview";
import { TagModule } from "primeng/tag";
import { TerminalModule } from "primeng/terminal";
import { TieredMenuModule } from "primeng/tieredmenu";
import { TimelineModule } from "primeng/timeline";
import { ToastModule } from "primeng/toast";
import { ToggleButtonModule } from "primeng/togglebutton";
import { ToolbarModule } from "primeng/toolbar";
import { TooltipModule } from "primeng/tooltip";
import { TreeModule } from "primeng/tree";
import { TreeTableModule } from "primeng/treetable";
import { VirtualScrollerModule } from "primeng/virtualscroller";

import { AppCodeModule } from "./app.code.component";
import { AppComponent } from "./app.component";
import { AppMainComponent } from "./app.main.component";
import { AppConfigComponent } from "./app.config.component";
import { AppMenuComponent } from "./app.menu.component";
import { AppMenuitemComponent } from "./app.menuitem.component";
import { AppTopBarComponent } from "./app.topbar.component";
import { AppFooterComponent } from "./app.footer.component";
import { AppHelpComponent } from "./pages/app.help.component";
import { AppNotfoundComponent } from "./pages/app.notfound.component";
import { AppErrorComponent } from "./pages/app.error.component";
import { AppAccessdeniedComponent } from "./pages/app.accessdenied.component";
import { AppLoginComponent } from "./pages/app.login.component";

import { BreadcrumbService } from "./app.breadcrumb.service";
import { MenuService } from "./app.menu.service";

import { DBConfig, NgxIndexedDBModule } from "ngx-indexed-db";
import { HttpErrorInterceptorProvider } from "./cadre/interceptor/http-error-interceptor";
import { AuthInterceptorService } from "./cadre/interceptor/auth-interceptor.service";
import { WindowComponent } from './cadre/view/window.component';
import { CadreTabComponent } from './cadre/view/cadre-tab.component';
import { TabCustomButtonComponent } from './cadre/view/tab-custom-button.component';
import { TabToolbarComponent } from './cadre/view/tab-toolbar.component';

import { MessageService } from "primeng/api";
import { FieldComponent } from './cadre/view/field.component';
import { WebDirective } from "./cadre/view/components/web.directive";
import { UIButton } from "./cadre/view/components/ui-button.component";
import { UITableDirectEditorComponent } from "./cadre/view/components/ui-table-direct-editor.component";
import { UIStringEditorComponent } from "./cadre/view/components/ui-string-editor.component";
import { UITextEditorComponent } from "./cadre/view/components/ui-text-editor.component";
import { UITableEditorComponent } from "./cadre/view/components/ui-table-editor.component";
import { UIListComponent } from "./cadre/view/components/ui-list.component";
import { UIYesNoEditorComponent } from "./cadre/view/components/ui-yes-no-editor.component";
import { UIIntegerEditorComponent } from "./cadre/view/components/ui-integer-editor.component";
import { UIDateEditorComponent } from "./cadre/view/components/ui-date-editor.component";
import { UIDateTimeEditorComponent } from "./cadre/view/components/ui-date-time-editor.component";
import { UIPasswordEditorComponent } from "./cadre/view/components/ui-password-editor.component";
import { UIAmountEditorComponent } from "./cadre/view/components/ui-amount-editor.component";
import { DashboarddemoComponent } from './cadre/view/dashboarddemo.component';
import { ProcessParamComponent } from "./cadre/view/process-param.component";
import { TabImportFileComponent } from "./cadre/view/tab-import-file.component";

const dbConfig: DBConfig = {
  name: "CADRE_DB",
  version: 1,
  objectStoresMeta: [
    {
      store: "AD_Table",
      storeConfig: { keyPath: "AD_Table_ID", autoIncrement: false },
      storeSchema: [
        { name: "TableName", keypath: "TableName", options: { unique: true } },
      ],
    },
    {
      store: "AD_Column",
      storeConfig: { keyPath: "AD_Column_ID", autoIncrement: false },
      storeSchema: [
        {
          name: "ColumnName",
          keypath: "ColumnName",
          options: { unique: false },
        },
        {
          name: "AD_Reference_ID",
          keypath: "AD_Reference_ID",
          options: { unique: false },
        },
        {
          name: "AD_Table_ID",
          keypath: "AD_Table_ID",
          options: { unique: false },
        },
        {
          name: "IsIdentifier",
          keypath: "IsIdentifier",
          options: { unique: false },
        },
      ],
    },
    {
      store: "AD_Window",
      storeConfig: { keyPath: "AD_Window_ID", autoIncrement: false },
      storeSchema: [
        { name: "Name", keypath: "Name", options: { unique: false } },
        {
          name: "Description",
          keypath: "Description",
          options: { unique: false },
        },
      ],
    },
    {
      store: "AD_Tab",
      storeConfig: { keyPath: "AD_Tab_ID", autoIncrement: false },
      storeSchema: [
        {
          name: "AD_Window_ID",
          keypath: "AD_Window_ID",
          options: { unique: false },
        },
        { name: "SeqNo", keypath: "SeqNo", options: { unique: false } },
        {
          name: "IsReadOnly",
          keypath: "IsReadOnly",
          options: { unique: false },
        },
        { name: "Name", keypath: "Name", options: { unique: false } },
        {
          name: "Description",
          keypath: "Description",
          options: { unique: false },
        },
        { name: "TabLevel", keypath: "TabLevel", options: { unique: false } },
        {
          name: "IsInsertRecord",
          keypath: "IsInsertRecord",
          options: { unique: false },
        },
        {
          name: "Parent_Column_ID",
          keypath: "Parent_Column_ID",
          options: { unique: false },
        },
        {
          name: "Parent_ColumnName",
          keypath: "Parent_ColumnName",
          options: { unique: false },
        },
        {
          name: "OrderByClause",
          keypath: "OrderByClause",
          options: { unique: false },
        },
      ],
    },
    {
      store: "AD_Field",
      storeConfig: { keyPath: "AD_Field_ID", autoIncrement: false },
      storeSchema: [
        { name: "AD_Tab_ID", keypath: "AD_Tab_ID", options: { unique: false } },
        {
          name: "IsSameLine",
          keypath: "IsSameLine",
          options: { unique: false },
        },
        {
          name: "IsReadOnly",
          keypath: "IsReadOnly",
          options: { unique: false },
        },
        { name: "Help", keypath: "Help", options: { unique: false } },
        {
          name: "Description",
          keypath: "Description",
          options: { unique: false },
        },
        {
          name: "AD_Column_ID",
          keypath: "AD_Column_ID",
          options: { unique: false },
        },
        {
          name: "IsDisplayed",
          keypath: "IsDisplayed",
          options: { unique: false },
        },
        {
          name: "AD_Reference_ID",
          keypath: "AD_Reference_ID",
          options: { unique: false },
        },
        {
          name: "AD_Reference_Value_ID",
          keypath: "AD_Reference_Value_ID",
          options: { unique: false },
        },
        {
          name: "IsMandatory",
          keypath: "IsMandatory",
          options: { unique: false },
        },
        {
          name: "DefaultValue",
          keypath: "DefaultValue",
          options: { unique: false },
        },
        {
          name: "DynamicValidation",
          keypath: "DynamicValidation",
          options: { unique: false },
        },
        { name: "SeqNo", keypath: "SeqNo", options: { unique: false } },
        { name: "Label", keypath: "Label", options: { unique: false } },
        {
          name: "BootstrapClass",
          keypath: "BootstrapClass",
          options: { unique: false },
        },
        {
          name: "Placeholder",
          keypath: "Placeholder",
          options: { unique: false },
        },
        {
          name: "IsDisplayedGrid",
          keypath: "IsDisplayedGrid",
          options: { unique: false },
        },
        {
          name: "ColumnName",
          keypath: "ColumnName",
          options: { unique: false },
        },
        {
          name: "AD_Process_ID",
          keypath: "AD_Process_ID",
          options: { unique: false },
        },
        {
          name: "ProcessValue",
          keypath: "ProcessValue",
          options: { unique: false },
        },
      ],
    },
  ],
};

@NgModule({
  imports: [
    NgxIndexedDBModule.forRoot(dbConfig),
    ReactiveFormsModule,

    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AccordionModule,
    AutoCompleteModule,
    AvatarModule,
    AvatarGroupModule,
    BadgeModule,
    BreadcrumbModule,
    ButtonModule,
    CalendarModule,
    CardModule,
    CarouselModule,
    CascadeSelectModule,
    ChartModule,
    CheckboxModule,
    ChipModule,
    ChipsModule,
    CodeHighlighterModule,
    ConfirmDialogModule,
    ConfirmPopupModule,
    ColorPickerModule,
    ContextMenuModule,
    DataViewModule,
    DialogModule,
    DividerModule,
    DropdownModule,
    FieldsetModule,
    FileUploadModule,
    FullCalendarModule,
    GalleriaModule,
    InplaceModule,
    InputNumberModule,
    InputMaskModule,
    InputSwitchModule,
    InputTextModule,
    InputTextareaModule,
    KnobModule,
    LightboxModule,
    ListboxModule,
    MegaMenuModule,
    MenuModule,
    MenubarModule,
    MessageModule,
    MessagesModule,
    MultiSelectModule,
    OrderListModule,
    OrganizationChartModule,
    OverlayPanelModule,
    PaginatorModule,
    PanelModule,
    PanelMenuModule,
    PasswordModule,
    PickListModule,
    ProgressBarModule,
    RadioButtonModule,
    RatingModule,
    RippleModule,
    ScrollPanelModule,
    ScrollTopModule,
    SelectButtonModule,
    SidebarModule,
    SkeletonModule,
    SlideMenuModule,
    SliderModule,
    SplitButtonModule,
    SplitterModule,
    StepsModule,
    TableModule,
    TabMenuModule,
    TabViewModule,
    TagModule,
    TerminalModule,
    TimelineModule,
    TieredMenuModule,
    ToastModule,
    ToggleButtonModule,
    ToolbarModule,
    TooltipModule,
    TreeModule,
    TreeTableModule,
    VirtualScrollerModule,
    AppCodeModule,

  ],
  declarations: [
    AppComponent,
    AppMainComponent,
    AppMenuComponent,
    AppMenuitemComponent,
    AppConfigComponent,
    AppTopBarComponent,
    AppFooterComponent,
    AppLoginComponent,
    AppHelpComponent,
    AppNotfoundComponent,
    AppErrorComponent,
    AppAccessdeniedComponent,
    WindowComponent,
    CadreTabComponent,
    TabCustomButtonComponent,
    TabToolbarComponent,
    FieldComponent,
    WebDirective,
    UITableDirectEditorComponent,
    UIStringEditorComponent,
    UITextEditorComponent,
    UITableEditorComponent,
    UIListComponent,
    UIYesNoEditorComponent,
    UIIntegerEditorComponent,
    UIDateEditorComponent,
    UIDateTimeEditorComponent,
    UIPasswordEditorComponent,
    UIAmountEditorComponent,
    UIButton,
    DashboarddemoComponent,
    ProcessParamComponent,
    TabImportFileComponent
  ],
  entryComponents: [
    UITableDirectEditorComponent,
    UIStringEditorComponent,
    UITextEditorComponent,
    UITableEditorComponent,
    UIListComponent,
    UIYesNoEditorComponent,
    UIIntegerEditorComponent,
    UIDateEditorComponent,
    UIDateTimeEditorComponent,
    UIPasswordEditorComponent,
    UIAmountEditorComponent,
    UIButton
  ],
  providers: [
    { provide: LocationStrategy, useClass: HashLocationStrategy },
   // CountryService,
   // CustomerService,
   // EventService,
   // IconService,
   // NodeService,
   // PhotoService,
    MessageService,
    MenuService,
    BreadcrumbService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true,
    },
    HttpErrorInterceptorProvider,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
