-- ============================================================
-- 角色管理权限种子数据
-- 清空旧 permission（仅4条dept测试数据），按模块重建49个权限点
-- 新增4个业务角色，为超级管理员分配全部权限
-- ============================================================
START TRANSACTION;

-- 1. 清空旧关联和权限
DELETE FROM tbl_role_permission;
DELETE FROM tbl_permission;
ALTER TABLE tbl_permission AUTO_INCREMENT = 1;

-- 2. 插入权限点（按模块分组）
-- 系统管理（用户/部门/角色）
INSERT INTO tbl_permission (permission) VALUES
('user:list'),('user:add'),('user:update'),('user:delete'),
('dept:list'),('dept:add'),('dept:update'),('dept:delete'),
('role:list'),('role:add'),('role:update'),('role:delete');

-- 商品管理（商品/分类/品牌/属性）
INSERT INTO tbl_permission (permission) VALUES
('goods:list'),('goods:add'),('goods:update'),('goods:delete'),
('category:list'),('category:add'),('category:update'),('category:delete'),
('brand:list'),('brand:add'),('brand:update'),('brand:delete'),
('attr:list'),('attr:add'),('attr:update'),('attr:delete');

-- 订单管理
INSERT INTO tbl_permission (permission) VALUES
('order:list'),('order:detail'),('order:ship'),
('order:updatePrice'),('order:cancel'),('order:delete');

-- 库存管理
INSERT INTO tbl_permission (permission) VALUES
('inventory:list'),('inventory:adjust');

-- 营销管理（优惠券/会员/广告）
INSERT INTO tbl_permission (permission) VALUES
('coupon:list'),('coupon:add'),('coupon:update'),('coupon:delete'),
('member:list'),('member:update'),
('advert:list'),('advert:add'),('advert:update'),('advert:delete');

-- 数据看板/分析
INSERT INTO tbl_permission (permission) VALUES
('dashboard:view'),('analysis:view');

-- 3. 新增业务角色
INSERT INTO tbl_role (role_name, descript, create_date, update_date) VALUES
('运营员',     '日常运营：查看看板、订单、会员、优惠券', NOW(), NOW()),
('商品管理员', '商品上下架、分类品牌属性维护',           NOW(), NOW()),
('订单管理员', '订单查看/改价/发货/取消/删除',             NOW(), NOW()),
('库存管理员', '库存查询与调整',                           NOW(), NOW());
-- 注：id 自增，原有 id=1~5 保留

-- 4. 超级管理员(id=1) 分配全部权限
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT 1, id FROM tbl_permission;

-- 5. 运营员(id=6)：看板+分析+订单查看+会员查看+优惠券查看
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT 6, id FROM tbl_permission WHERE permission IN (
  'dashboard:view','analysis:view',
  'order:list','order:detail',
  'member:list','coupon:list'
);

-- 6. 商品管理员(id=7)：商品/分类/品牌/属性 全部 + 库存查看
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT 7, id FROM tbl_permission WHERE permission LIKE 'goods:%'
  OR permission LIKE 'category:%' OR permission LIKE 'brand:%'
  OR permission LIKE 'attr:%' OR permission = 'inventory:list';

-- 7. 订单管理员(id=8)：订单全部 + 库存查看
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT 8, id FROM tbl_permission WHERE permission LIKE 'order:%'
  OR permission = 'inventory:list';

-- 8. 库存管理员(id=9)：库存全部
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT 9, id FROM tbl_permission WHERE permission LIKE 'inventory:%';

COMMIT;

-- 验证
SELECT COUNT(*) AS permission_cnt FROM tbl_permission;
SELECT COUNT(*) AS role_cnt FROM tbl_role;
SELECT COUNT(*) AS rp_cnt FROM tbl_role_permission;
SELECT id, role_name FROM tbl_role ORDER BY id;
