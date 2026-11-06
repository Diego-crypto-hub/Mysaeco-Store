let cart = [];

function addToCartOnly(id, nome, precoStr, imagem) {
    const precoNum = 65.00;
    const existingItem = cart.find(item => item.id === id);
    
    if (existingItem) {
        existingItem.qtd += 1;
    } else {
        cart.push({ id, nome, preco: precoNum, imagem, qtd: 1 });
    }
    
    updateCartUI();
}

function buyNow(id, nome, precoStr, imagem) {
    addToCartOnly(id, nome, precoStr, imagem);
    openCheckoutModal();
}

function updateCartUI() {
    const cartCount = document.getElementById('cartCount');
    const cartBody = document.getElementById('cartBody');
    const cartTotalPrice = document.getElementById('cartTotalPrice');

    let totalItems = 0;
    let totalPrice = 0;

    if (cart.length === 0) {
        cartBody.innerHTML = '<p style="color: var(--text-secondary); text-align: center; margin-top: 3rem;">O seu carrinho está vazio.</p>';
    } else {
        cartBody.innerHTML = '';
        cart.forEach((item, index) => {
            totalItems += item.qtd;
            totalPrice += item.preco * item.qtd;

            cartBody.innerHTML += `
                <div class="cart-item">
                    <img src="${item.imagem}" alt="${item.nome}">
                    <div class="cart-item-info">
                        <h4>${item.nome}</h4>
                        <p>R$ ${item.preco.toFixed(2).replace('.', ',')}</p>
                        <div class="cart-item-controls">
                            <button onclick="changeQtd(${index}, -1)">-</button>
                            <span>${item.qtd}</span>
                            <button onclick="changeQtd(${index}, 1)">+</button>
                        </div>
                    </div>
                </div>
            `;
        });
    }

    cartCount.innerText = totalItems;
    cartTotalPrice.innerText = `R$ ${totalPrice.toFixed(2).replace('.', ',')}`;
    document.getElementById('checkoutModalTotal').innerText = `Total: R$ ${totalPrice.toFixed(2).replace('.', ',')}`;
}

function changeQtd(index, delta) {
    cart[index].qtd += delta;
    if (cart[index].qtd <= 0) {
        cart.splice(index, 1);
    }
    updateCartUI();
}

function openCartDrawer() {
    const overlay = document.getElementById('cartDrawerOverlay');
    overlay.style.display = 'flex';
    setTimeout(() => overlay.classList.add('active'), 10);
}

function closeCartDrawer() {
    const overlay = document.getElementById('cartDrawerOverlay');
    overlay.classList.remove('active');
    setTimeout(() => overlay.style.display = 'none', 300);
}

function openCheckoutModal() {
    if (cart.length === 0) {
        alert('O seu carrinho está vazio!');
        return;
    }
    closeCartDrawer();
    const modal = document.getElementById('checkoutModal');
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('active'), 10);
}

function closeCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    modal.classList.remove('active');
    setTimeout(() => modal.style.display = 'none', 300);
}

function selectPayment(type) {
    document.getElementById('pixDetails').classList.remove('active');
    document.getElementById('cardDetails').classList.remove('active');
    document.getElementById('boletoDetails').classList.remove('active');

    if (type === 'pix') document.getElementById('pixDetails').classList.add('active');
    if (type === 'card') document.getElementById('cardDetails').classList.add('active');
    if (type === 'boleto') document.getElementById('boletoDetails').classList.add('active');
}

function finalizeOrder() {
    alert('🎉 Pedido realizado com sucesso! Obrigado por comprar na MYSA & co.');
    cart = [];
    updateCartUI();
    closeCheckoutModal();
}

function openProductModal(nome, detalhes, preco) {
    document.getElementById('modalTitle').innerText = nome;
    document.getElementById('modalDesc').innerText = detalhes;
    document.getElementById('modalPrice').innerText = preco;
    const modal = document.getElementById('productModal');
    modal.style.display = 'flex';
    setTimeout(() => modal.classList.add('active'), 10);
}

function closeProductModal() {
    const modal = document.getElementById('productModal');
    modal.classList.remove('active');
    setTimeout(() => modal.style.display = 'none', 300);
}

// Funções atualizadas para a abertura do Modal "A Essência" (sem as antigas animações de delay)
function openEssenceModal() {
    const modal = document.getElementById('essenceModal');
    modal.style.display = 'flex';
    setTimeout(() => {
        modal.classList.add('active');
    }, 10);
}

function closeEssenceModal() {
    const modal = document.getElementById('essenceModal');
    modal.classList.remove('active');
    setTimeout(() => modal.style.display = 'none', 300);
}

// Efeito de revelação no scroll (Intersection Observer)
document.addEventListener("DOMContentLoaded", function() {
    const observerOptions = { threshold: 0.15 };
    const observer = new IntersectionObserver(function(entries, observer) {
        entries.forEach((entry, index) => {
            if (entry.isIntersecting) {
                setTimeout(() => {
                    entry.target.classList.add('reveal');
                }, index * 120);
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.card').forEach(card => {
        observer.observe(card);
    });
});