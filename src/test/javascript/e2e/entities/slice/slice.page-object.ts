import { browser, ExpectedConditions, element, by, ElementFinder } from 'protractor';

export class SliceComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-slice div table .btn-danger'));
  title = element.all(by.css('jhi-slice div h2#page-heading span')).first();

  async clickOnCreateButton(timeout?: number) {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(timeout?: number) {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons() {
    return this.deleteButtons.count();
  }

  async getTitle() {
    return this.title.getText();
  }
}

export class SliceMakePage {
  pageTitle = element(by.id('jhi-slice-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  androidVersionInput = element(by.id('field_androidVersion'));
  androidClassNameInput = element(by.id('field_androidClassName'));
  entryMethodsInput = element(by.id('field_entryMethods'));
  seedStatementsInput = element(by.id('field_seedStatements'));
  sliceInput = element(by.id('field_slice'));
  logInput = element(by.id('field_log'));
  threadIdInput = element(by.id('field_threadId'));
  runningInput = element(by.id('field_running'));
  cfaOptionNameInput = element(by.id('field_cfaOptionName'));
  cfaOptionTypeSelect = element(by.id('field_cfaOptionType'));
  cfaOptionLevelInput = element(by.id('field_cfaOptionLevel'));
  reflectionOptionsSelect = element(by.id('field_reflectionOptions'));
  dataDependenceOptionsSelect = element(by.id('field_dataDependenceOptions'));
  controlDependenceOptionsSelect = element(by.id('field_controlDependenceOptions'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setAndroidVersionInput(androidVersion) {
    await this.androidVersionInput.sendKeys(androidVersion);
  }

  async getAndroidVersionInput() {
    return await this.androidVersionInput.getAttribute('value');
  }

  async setAndroidClassNameInput(androidClassName) {
    await this.androidClassNameInput.sendKeys(androidClassName);
  }

  async getAndroidClassNameInput() {
    return await this.androidClassNameInput.getAttribute('value');
  }

  async setEntryMethodsInput(entryMethods) {
    await this.entryMethodsInput.sendKeys(entryMethods);
  }

  async getEntryMethodsInput() {
    return await this.entryMethodsInput.getAttribute('value');
  }

  async setSeedStatementsInput(seedStatements) {
    await this.seedStatementsInput.sendKeys(seedStatements);
  }

  async getSeedStatementsInput() {
    return await this.seedStatementsInput.getAttribute('value');
  }

  async setSliceInput(slice) {
    await this.sliceInput.sendKeys(slice);
  }

  async getSliceInput() {
    return await this.sliceInput.getAttribute('value');
  }

  async setLogInput(log) {
    await this.logInput.sendKeys(log);
  }

  async getLogInput() {
    return await this.logInput.getAttribute('value');
  }

  async setThreadIdInput(threadId) {
    await this.threadIdInput.sendKeys(threadId);
  }

  async getThreadIdInput() {
    return await this.threadIdInput.getAttribute('value');
  }

  getRunningInput(timeout?: number) {
    return this.runningInput;
  }
  async setCfaOptionNameInput(cfaOptionName) {
    await this.cfaOptionNameInput.sendKeys(cfaOptionName);
  }

  async getCfaOptionNameInput() {
    return await this.cfaOptionNameInput.getAttribute('value');
  }

  async setCfaOptionTypeSelect(cfaOptionType) {
    await this.cfaOptionTypeSelect.sendKeys(cfaOptionType);
  }

  async getCfaOptionTypeSelect() {
    return await this.cfaOptionTypeSelect.element(by.css('option:checked')).getText();
  }

  async cfaOptionTypeSelectLastOption(timeout?: number) {
    await this.cfaOptionTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setCfaOptionLevelInput(cfaOptionLevel) {
    await this.cfaOptionLevelInput.sendKeys(cfaOptionLevel);
  }

  async getCfaOptionLevelInput() {
    return await this.cfaOptionLevelInput.getAttribute('value');
  }

  async setReflectionOptionsSelect(reflectionOptions) {
    await this.reflectionOptionsSelect.sendKeys(reflectionOptions);
  }

  async getReflectionOptionsSelect() {
    return await this.reflectionOptionsSelect.element(by.css('option:checked')).getText();
  }

  async reflectionOptionsSelectLastOption(timeout?: number) {
    await this.reflectionOptionsSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setDataDependenceOptionsSelect(dataDependenceOptions) {
    await this.dataDependenceOptionsSelect.sendKeys(dataDependenceOptions);
  }

  async getDataDependenceOptionsSelect() {
    return await this.dataDependenceOptionsSelect.element(by.css('option:checked')).getText();
  }

  async dataDependenceOptionsSelectLastOption(timeout?: number) {
    await this.dataDependenceOptionsSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setControlDependenceOptionsSelect(controlDependenceOptions) {
    await this.controlDependenceOptionsSelect.sendKeys(controlDependenceOptions);
  }

  async getControlDependenceOptionsSelect() {
    return await this.controlDependenceOptionsSelect.element(by.css('option:checked')).getText();
  }

  async controlDependenceOptionsSelectLastOption(timeout?: number) {
    await this.controlDependenceOptionsSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async save(timeout?: number) {
    await this.saveButton.click();
  }

  async cancel(timeout?: number) {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class SliceDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-slice-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-slice'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
