import { browser, ExpectedConditions, element, by, ElementFinder } from 'protractor';

export class CFAOptionComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-cfa-option div table .btn-danger'));
  title = element.all(by.css('jhi-cfa-option div h2#page-heading span')).first();

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

export class CFAOptionUpdatePage {
  pageTitle = element(by.id('jhi-cfa-option-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  typeSelect = element(by.id('field_type'));
  keyInput = element(by.id('field_key'));
  descriptionInput = element(by.id('field_description'));
  cfaLevelInput = element(by.id('field_cfaLevel'));
  isDefaultInput = element(by.id('field_isDefault'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setTypeSelect(type) {
    await this.typeSelect.sendKeys(type);
  }

  async getTypeSelect() {
    return await this.typeSelect.element(by.css('option:checked')).getText();
  }

  async typeSelectLastOption(timeout?: number) {
    await this.typeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setKeyInput(key) {
    await this.keyInput.sendKeys(key);
  }

  async getKeyInput() {
    return await this.keyInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return await this.descriptionInput.getAttribute('value');
  }

  async setCfaLevelInput(cfaLevel) {
    await this.cfaLevelInput.sendKeys(cfaLevel);
  }

  async getCfaLevelInput() {
    return await this.cfaLevelInput.getAttribute('value');
  }

  getIsDefaultInput(timeout?: number) {
    return this.isDefaultInput;
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

export class CFAOptionDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-cFAOption-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-cFAOption'));

  async getDialogTitle() {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(timeout?: number) {
    await this.confirmButton.click();
  }
}
