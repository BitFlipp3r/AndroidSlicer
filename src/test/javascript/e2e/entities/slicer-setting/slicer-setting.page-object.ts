import { element, by, ElementFinder } from 'protractor';

export class SlicerSettingComponentsPage {
  title = element.all(by.css('jhi-slicer-setting div h2#page-heading span')).first();

  async getTitle() {
    return this.title.getText();
  }
}

export class SlicerSettingUpdatePage {
  pageTitle = element(by.id('jhi-slicer-setting-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));
  keyInput = element(by.id('field_key'));
  valueInput = element(by.id('field_value'));
  descriptionInput = element(by.id('field_description'));

  async getPageTitle() {
    return this.pageTitle.getText();
  }

  async setKeyInput(key) {
    await this.keyInput.sendKeys(key);
  }

  async getKeyInput() {
    return await this.keyInput.getAttribute('value');
  }

  async setValueInput(value) {
    await this.valueInput.sendKeys(value);
  }

  async getValueInput() {
    return await this.valueInput.getAttribute('value');
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return await this.descriptionInput.getAttribute('value');
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
