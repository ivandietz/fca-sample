package fca_sample;

import org.eclipse.swt.widgets.TreeItem;

public class TreeUtils {

  public static void checkPath(TreeItem item, boolean checked, boolean grayed) {
    if (item == null) return;
    if (grayed) {
      checked = true;
    } else {
      int index = 0;
      TreeItem[] items = item.getItems();
      while (index < items.length) {
        TreeItem child = items[index];
        if (child.getGrayed() || checked != child.getChecked()) {
          checked = grayed = true;
          break;
        }
        index++;
      }
    }
    item.setChecked(checked);
    item.setGrayed(grayed);
    checkPath(item.getParentItem(), checked, grayed);
  }

  public static void checkItems(TreeItem item, boolean checked) {
    item.setGrayed(false);
    item.setChecked(checked);
    TreeItem[] items = item.getItems();
    for (int i = 0; i < items.length; i++) {
      checkItems(items[i], checked);
    }
  }
}
