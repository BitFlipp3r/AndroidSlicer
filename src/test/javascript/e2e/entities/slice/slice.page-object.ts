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

export class SliceUpdatePage {
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
  reflectionSelect = element(by.id('field_reflection'));
  dataDependenceSelect = element(by.id('field_dataDependence'));
  controlDependenceSelect = element(by.id('field_controlDependence'));

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

  async reflectionSelectLastOption(timeout?: number) {
    await this.reflectionSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async reflectionSelectOption(option) {
    await this.reflectionSelect.sendKeys(option);
  }

  getReflectionSelect(): ElementFinder {
    return this.reflectionSelect;
  }

  async getReflectionSelectedOption() {
    return await this.reflectionSelect.element(by.css('option:checked')).getText();
  }

  async dataDependenceSelectLastOption(timeout?: number) {
    await this.dataDependenceSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async dataDependenceSelectOption(option) {
    await this.dataDependenceSelect.sendKeys(option);
  }

  getDataDependenceSelect(): ElementFinder {
    return this.dataDependenceSelect;
  }

  async getDataDependenceSelectedOption() {
    return await this.dataDependenceSelect.element(by.css('option:checked')).getText();
  }

  async controlDependenceSelectLastOption(timeout?: number) {
    await this.controlDependenceSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async controlDependenceSelectOption(option) {
    await this.controlDependenceSelect.sendKeys(option);
  }

  getControlDependenceSelect(): ElementFinder {
    return this.controlDependenceSelect;
  }

  async getControlDependenceSelectedOption() {
    return await this.controlDependenceSelect.element(by.css('option:checked')).getText();
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
