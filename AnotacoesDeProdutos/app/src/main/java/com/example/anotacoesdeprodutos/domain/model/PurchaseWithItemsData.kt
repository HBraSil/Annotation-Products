package com.example.anotacoesdeprodutos.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.anotacoesdeprodutos.data.entity.CartItemEntity
import com.example.anotacoesdeprodutos.data.entity.ProductEntity
import com.example.anotacoesdeprodutos.data.entity.PurchaseEntity
import com.example.anotacoesdeprodutos.data.entity.toCartItemDomain
import com.example.anotacoesdeprodutos.data.entity.toDomain
import com.example.anotacoesdeprodutos.data.entity.toProductDomain

/**
 * Representa uma compra completa, incluindo seus itens e os respectivos produtos.
 * O Room utiliza as anotações @Embedded e @Relation para resolver automaticamente o mapeamento
 * aninhado entre as tabelas de compra, itens de carrinho e produtos.
 *
 * - @Embedded utiliza as colunas retornadas pela consulta principal, feita na interface DAO, para construir, neste caso, um PurchaseEntity.
 *
 * - @Relation realiza uma nova consulta, neste caso na CartItemEntity, comparando o id do objeto recém-construído (@Embedded purchase) com a coluna purchaseId da tabela CartItemEntity
 */
data class PurchaseWithItemsData(
    @Embedded val purchase: PurchaseEntity = PurchaseEntity(),

    @Relation(
        parentColumn = "id",
        entityColumn = "purchaseId",
        entity = CartItemEntity::class
    )
    val items: List<CartItemWithProductData> = emptyList(),
)

data class CartItemWithProductData(
    @Embedded val cartItem: CartItemEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity,
)

fun PurchaseWithItemsData.toDomain() = PurchaseWithItemsDomain(
    purchase = purchase.toDomain(),
    items = items.map { it.toDomain() }
)

fun CartItemWithProductData.toDomain() = CartItemWithProductDomain(
    cartItem = cartItem.toCartItemDomain(),
    product = product.toProductDomain()
)
