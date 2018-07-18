// excerpt
// long and complex, maybe better suited to a separate class,
// but the advantage here is that it gets access to members of the enclosing class
ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

            protected boolean isElevated = false;
            protected float originalElevation = 0;
            protected float activeElevationChange = 8f;


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();
                //todo: actually update database
                CategorySortOrder order = viewModel.getCurrentSort().getValue();
                String moved = order.categoryOrder.remove(fromPos);
                order.categoryOrder.add(toPos, moved);
                mCategoryRepository.updateSortOrder(order);
                recyclerViewAdapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                CategorySortOrder selectedOrder = viewModel.getCurrentSort().getValue();
                selectedOrder.categoryOrder.remove(pos);
                mCategoryRepository.updateSortOrder(selectedOrder);
                recyclerViewAdapter.notifyItemRemoved(pos);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (dX == 0 && dY != 0) {
                    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    return;
                }

                final View fg = ((CategorySortHolder) viewHolder).mBinding.categoryForeground;

                getDefaultUIUtil().onDrawOver(c, recyclerView, fg, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    getDefaultUIUtil().onSelected(((CategorySortHolder) viewHolder).mBinding.categoryForeground);
                    updateElevation(viewHolder, true);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // if we are dragging vertically
                if (dX == 0 && dY != 0) {
                    // prevents default elevation change (?)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
                    return;
                }
                final View fg = ((CategorySortHolder) viewHolder).mBinding.categoryForeground;

                getDefaultUIUtil().onDraw(c, recyclerView, fg, dX, dY, actionState, isCurrentlyActive);

            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                updateElevation(viewHolder, false);
            }

            protected void updateElevation(@NonNull RecyclerView.ViewHolder holder, boolean elevate) {
                if (elevate) {
                    originalElevation = ViewCompat.getElevation(holder.itemView);
                    float newElevation = activeElevationChange;
                    ViewCompat.setElevation(holder.itemView, newElevation);
                    isElevated = true;
                } else {
                    ViewCompat.setElevation(holder.itemView, 0);
                    originalElevation = 0;
                    isElevated = false;
                }
            }

            /**
             * Finds the elevation of the highest visible viewHolder to make sure the elevated view
             * from {@link #updateElevation(RecyclerView.ViewHolder, boolean)} is above
             * all others.
             *
             * @param recyclerView The RecyclerView to use when determining the height of all the visible ViewHolders
             */
            protected float findMaxElevation(@NonNull RecyclerView recyclerView) {
                float maxChildElevation = 0;

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    float elevation = ViewCompat.getElevation(child);

                    if (elevation > maxChildElevation) {
                        maxChildElevation = elevation;
                    }
                }

                return maxChildElevation;
            }
        });