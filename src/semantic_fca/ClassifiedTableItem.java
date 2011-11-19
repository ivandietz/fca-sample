package semantic_fca;

import org.eclipse.swt.widgets.TableItem;

public class ClassifiedTableItem {

		private String criteria;
		private TableItem item;
		
		public ClassifiedTableItem(String criteria, TableItem item) {
			this.criteria = criteria;
			this.item = item;
		}
		
		public String getCriteria() {
			return criteria;
		}
		
		public TableItem getItem() {
			return item;
		}
}
