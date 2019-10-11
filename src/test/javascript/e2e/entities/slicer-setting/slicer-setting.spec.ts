// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { SlicerSettingComponentsPage, SlicerSettingDeleteDialog, SlicerSettingUpdatePage } from './slicer-setting.page-object';

const expect = chai.expect;

describe('SlicerSetting e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let slicerSettingComponentsPage: SlicerSettingComponentsPage;
  let slicerSettingUpdatePage: SlicerSettingUpdatePage;
  let slicerSettingDeleteDialog: SlicerSettingDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load SlicerSettings', async () => {
    await navBarPage.goToEntity('slicer-setting');
    slicerSettingComponentsPage = new SlicerSettingComponentsPage();
    await browser.wait(ec.visibilityOf(slicerSettingComponentsPage.title), 5000);
    expect(await slicerSettingComponentsPage.getTitle()).to.eq('Slicer Settings');
  });

  it('should load create SlicerSetting page', async () => {
    await slicerSettingComponentsPage.clickOnCreateButton();
    slicerSettingUpdatePage = new SlicerSettingUpdatePage();
    expect(await slicerSettingUpdatePage.getPageTitle()).to.eq('Create or edit a Slicer Setting');
    await slicerSettingUpdatePage.cancel();
  });

  it('should create and save SlicerSettings', async () => {
    const nbButtonsBeforeCreate = await slicerSettingComponentsPage.countDeleteButtons();

    await slicerSettingComponentsPage.clickOnCreateButton();
    await promise.all([slicerSettingUpdatePage.setKeyInput('key'), slicerSettingUpdatePage.setValueInput('value')]);
    expect(await slicerSettingUpdatePage.getKeyInput()).to.eq('key', 'Expected Key value to be equals to key');
    expect(await slicerSettingUpdatePage.getValueInput()).to.eq('value', 'Expected Value value to be equals to value');
    await slicerSettingUpdatePage.save();
    expect(await slicerSettingUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await slicerSettingComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last SlicerSetting', async () => {
    const nbButtonsBeforeDelete = await slicerSettingComponentsPage.countDeleteButtons();
    await slicerSettingComponentsPage.clickOnLastDeleteButton();

    slicerSettingDeleteDialog = new SlicerSettingDeleteDialog();
    expect(await slicerSettingDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Slicer Setting?');
    await slicerSettingDeleteDialog.clickOnConfirmButton();

    expect(await slicerSettingComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
