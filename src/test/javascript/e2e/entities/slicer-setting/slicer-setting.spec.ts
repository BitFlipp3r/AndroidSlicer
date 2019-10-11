// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { SlicerSettingComponentsPage } from './slicer-setting.page-object';

const expect = chai.expect;

describe('SlicerSetting e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let slicerSettingComponentsPage: SlicerSettingComponentsPage;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load SlicerSettings', async () => {
    await navBarPage.goToEntity('slicer-settings');
    slicerSettingComponentsPage = new SlicerSettingComponentsPage();
    await browser.wait(ec.visibilityOf(slicerSettingComponentsPage.title), 5000);
    expect(await slicerSettingComponentsPage.getTitle()).to.eq('Slicer Settings');
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
