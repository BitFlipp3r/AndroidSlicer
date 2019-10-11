import { element, by, ElementFinder } from 'protractor';

export class SlicerOptionComponentsPage {
  title = element.all(by.css('jhi-slicer-option div h2#page-heading span')).first();

  async getTitle() {
    return this.title.getText();
  }
}

export class SlicerOptionUpdatePage {
  pageTitle = element(by.id('jhi-slicer-option-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  typeSelect = element(by.id('field_type'));
  keyInput = element(by.id('field_key'));
  descriptionInput = element(by.id('field_description'));
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
