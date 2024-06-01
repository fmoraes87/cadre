import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';
import {map, share, catchError} from 'rxjs/operators';
import { TreeNodeModel } from '../domain/treenode.model';
import { CriteriaBuilder } from '../filter/criteria-builder.model';
import { Restrictions } from '../filter/restrictions.factory';
import { Env } from '../util/env';
import { POService } from './po.service';

@Injectable({
    providedIn: 'root',
})
export class MenuService {

    constructor(private poService: POService) { }

    getMenu(): Promise<TreeNodeModel[]> {
        return new Promise<TreeNodeModel[]>((resolve) => {
            let menu: TreeNodeModel [] = [];
            //Get tabs
            let filter = new CriteriaBuilder()
                .from(TreeNodeModel.tableName)
                .addCriterion(Restrictions.eq("IsSummary", true))
                .addCriterion(Restrictions.eq("AD_Tree_ID", Env.getGlobalContext(Env.AD_MENU_ID)))
                .addCriterion(Restrictions.eq("AD_TreeNode_Parent_ID", 0))
                .orderBy("SeqNo")

                .build();

            //Get Table Info
            this.poService.getTableData(filter,false).pipe()
                .subscribe(
                    (resp: any) => {
                      let data = resp.value;
                        if (data) {
                            let keys: any[] = data;

                            keys.forEach(treeNodeJSON => {
                                let treeNodeModel = new TreeNodeModel();
                                treeNodeModel.loadFromJSON(treeNodeJSON);
                                menu.push(treeNodeModel);
                            });
                        }

                        resolve(menu)

                    }
                );
        });

    }


    getChildren(treeNode: TreeNodeModel): Observable<TreeNodeModel[]>{
        let children: TreeNodeModel [] = [];
        //Get tabs
        let filter = new CriteriaBuilder()
            .from(TreeNodeModel.tableName)
            .addCriterion(Restrictions.eq("AD_TreeNode_Parent_ID", treeNode.ad_treenode_id))
            .orderBy("SeqNo")
            .build();

        //Get Table Info
        return this.poService.getTableData(filter,false)
            .pipe(//catchError(this.errorHandler.handleError),
               map(resp => {
                  let data = resp.value;
                  if (data) {
                        let keys: any[] = data;

                        keys.forEach(treeNodeJSON => {
                            let treeNodeModel = new TreeNodeModel();
                            treeNodeModel.loadFromJSON(treeNodeJSON);
                            children.push(treeNodeModel);
                        });
                    }

                    return children;

                    }
                ), share()
            );

    }
}
